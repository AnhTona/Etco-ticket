package com.esco.etco.service.impl;

import com.esco.etco.entity.ChatMessage;
import com.esco.etco.entity.Event;
import com.esco.etco.entity.Genre;
import com.esco.etco.entity.Order;
import com.esco.etco.entity.Ticket;
import com.esco.etco.entity.request.ReqChatDTO;
import com.esco.etco.entity.response.chat.ResChatDTO;
import com.esco.etco.repository.*;
import com.esco.etco.service.*;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.error.IdInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AIAgentServiceImpl implements AIAgentService {

    @Value("${etco.agent.max-iterations}")
    private int maxIterations;

    @Value("${etco.agent.temperature}")
    private double temperature;

    private final OllamaService ollamaService;
    private final RAGService ragService;
    private final ChatMessageRepository chatMessageRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final GenreRepository genreRepository;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    public AIAgentServiceImpl(OllamaService ollamaService,
                              RAGService ragService,
                              ChatMessageRepository chatMessageRepository,
                              EventRepository eventRepository,
                              TicketRepository ticketRepository,
                              OrderRepository orderRepository,
                              GenreRepository genreRepository,
                              RecommendationService recommendationService,
                              UserRepository userRepository) {
        this.ollamaService = ollamaService;
        this.ragService = ragService;
        this.chatMessageRepository = chatMessageRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.genreRepository = genreRepository;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    @Override
    public ResChatDTO chat(ReqChatDTO dto) throws IdInvalidException {
        String sessionId = dto.getSessionId() != null ? dto.getSessionId() : UUID.randomUUID().toString();
        saveMessage(sessionId, "user", dto.getMessage(), false, null, 0);

        List<String> toolsUsed = new ArrayList<>();
        List<ResChatDTO.SourceDTO> sources = new ArrayList<>();
        StringBuilder allContext = new StringBuilder();

        List<String> intents = detectIntent(dto.getMessage());

        // Ưu tiên 1: Chạy RAG Search cho kiến thức tĩnh (Chính sách, FAQ) NẾU intent là RAG_SEARCH
        // Rút gọn việc lúc nào cũng chạy RAG gây lãng phí nếu người dùng chỉ hỏi mua vé
        if (intents.contains("RAG_SEARCH") || intents.isEmpty()) {
            String ragResult = executeRAGSearch(dto.getMessage(), sources);
            if (!ragResult.isEmpty()) {
                toolsUsed.add("RAG_SEARCH");
                allContext.append("=== KIẾN THỨC HỆ THỐNG ===\n").append(ragResult).append("\n");
            }
        }

        // Xử lý tìm kiếm Sự kiện/Vé/Thể loại
        if (intents.contains("SEARCH_EVENTS") || intents.contains("SEARCH_TICKETS") || intents.contains("SEARCH_BY_GENRE")) {

            // Thử tìm theo Tên sự kiện cụ thể trước
            String eventResult = executeEventSearch(dto.getMessage());
            boolean foundByEvent = !eventResult.startsWith("Loi") && !eventResult.contains("không có") && !eventResult.contains("không khớp");

            if (foundByEvent) {
                toolsUsed.add("SEARCH_EVENTS");
                allContext.append("=== SỰ KIỆN KHỚP TÊN ===\n").append(eventResult).append("\n");

                // Nếu thấy sự kiện thì tìm vé luôn
                String ticketResult = executeTicketSearch(dto.getMessage());
                if (!ticketResult.startsWith("Loi") && !ticketResult.contains("chưa có thông tin")) {
                    toolsUsed.add("SEARCH_TICKETS");
                    allContext.append("=== THÔNG TIN VÉ ===\n").append(ticketResult).append("\n");
                }
            } else {
                // FALLBACK - Nếu không tìm thấy tên, tự động tìm theo Thể loại (Genre)
                log.info(">>> AGENT: No event name matched, falling back to Genre search...");
                String genreResult = executeGenreSearch(dto.getMessage());

                if (!genreResult.contains("Lỗi") && !genreResult.contains("chưa xác định")) {
                    toolsUsed.add("SEARCH_BY_GENRE");
                    allContext.append("=== SỰ KIỆN THEO THỂ LOẠI ===\n").append(genreResult).append("\n");
                }
            }
        }

        // Xử lý gợi ý sự kiện (Recommendation Agentic RAG)
        if (intents.contains("RECOMMEND_EVENTS")) {
            log.info(">>> AGENT: Intent RECOMMEND_EVENTS detected");
            String recommendResult = executeRecommendationSearch(dto.getMessage());
            if (!recommendResult.isEmpty()) {
                toolsUsed.add("RECOMMEND_EVENTS");
                allContext.append("=== GỢI Ý CÁ NHÂN HÓA (AGENTIC RAG) ===\n").append(recommendResult).append("\n");
            }
        }

        // 3. Tra cứu đơn hàng
        if (intents.contains("LOOKUP_ORDER")) {
            toolsUsed.add("LOOKUP_ORDER");
            allContext.append("=== ĐƠN HÀNG ===\n").append(executeOrderLookup(dto.getMessage())).append("\n");
        }

        // 4. Sinh câu trả lời cuối cùng
        String answer = generateAnswer(dto.getMessage(), allContext.toString(), sources, loadHistory(sessionId));
        saveMessage(sessionId, "assistant", answer, false, String.join(",", toolsUsed), 1);

        return buildResponse(answer, sessionId, 1, toolsUsed, sources);
    }

    @Override
    public ResChatDTO chatWithImage(String message, String sessionId, MultipartFile image)
            throws Exception {
        sessionId = sessionId != null ? sessionId : UUID.randomUUID().toString();
        log.info(">>> AGENT VISION: session={}, message=\"{}\"", sessionId, message);

        // lưu tin nhắn user
        saveMessage(sessionId, "user", message + " [kèm ảnh]", true, null, 0);

        List<String> toolsUsed = new ArrayList<>();
        List<ResChatDTO.SourceDTO> sources = new ArrayList<>();

        // convert ảnh -> base64
        String base64 = Base64.getEncoder().encodeToString(image.getBytes());

        // Gửi ảnh + conversation history để nhớ ngữ cảnh
        List<Map<String, Object>> visionMessages = new ArrayList<>();
        visionMessages.add(buildSystemMessage());

        // thêm history để nhớ ngữ cảnh trước đó
        List<Map<String, Object>> history = loadHistory(sessionId);
        int historyLimit = Math.min(history.size(), 6);
        for (int i = history.size() - historyLimit; i < history.size(); i++) {
            visionMessages.add(history.get(i));
        }

        // tin nhắn kèm ảnh
        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        userMsg.put("images", List.of(base64));
        visionMessages.add(userMsg);

        String imageAnalysis = this.ollamaService.chatWithImage(visionMessages, temperature);
        toolsUsed.add("VISION_ANALYSIS");
        log.info(">>> AGENT VISION: analysis length={}", imageAnalysis.length());

        String combinedQuery = message + " " + imageAnalysis;
        String ragContext = executeRAGSearch(combinedQuery, sources);
        if (!ragContext.isEmpty()) {
            toolsUsed.add("RAG_SEARCH");
        }

        List<Map<String, Object>> finalMessages = new ArrayList<>();
        finalMessages.add(buildSystemMessage());

        StringBuilder prompt = new StringBuilder();
        prompt.append("Người dùng gửi ảnh và hỏi: \"").append(message).append("\"\n\n");
        prompt.append("Kết quả phân tích ảnh:\n").append(imageAnalysis).append("\n\n");
        if (!ragContext.isEmpty()) {
            prompt.append("Thông tin bổ sung từ tài liệu:\n").append(ragContext).append("\n\n");
        }
        prompt.append("Hãy trả lời câu hỏi dựa trên phân tích ảnh và thông tin bổ sung. ");
        prompt.append("Trả lời bằng tiếng Việt, dễ hiểu.");

        finalMessages.add(Map.of("role", "user", "content", prompt.toString()));

        String answer = this.ollamaService.chat(finalMessages, temperature);

        // lưu
        saveMessage(sessionId, "assistant", answer, false,
                String.join(",", toolsUsed), 1);

        return buildResponse(answer, sessionId, 1, toolsUsed, sources);
    }

    private List<String> detectIntent(String message) {
        List<String> intents = new ArrayList<>();
        String lower = message.toLowerCase();

        // Gợi ý sự kiện
        if (lower.contains("gợi ý") || lower.contains("giới thiệu") || lower.contains("recommend") || lower.contains("có gì hay") || lower.contains("sự kiện nào giống")) {
            intents.add("RECOMMEND_EVENTS");
        }

        if (lower.contains("thể loại") || lower.contains("genre") || lower.contains("loại hình")
                || lower.contains("ca nhạc") || lower.contains("hội thảo") || lower.contains("triển lãm")) {
            intents.add("SEARCH_BY_GENRE");
        }

        if (lower.contains("sự kiện") || lower.contains("event")
                || lower.contains("show") || lower.contains("concert")
                || lower.contains("ca sĩ") || lower.contains("nghệ sĩ")
                || lower.contains("lịch") || lower.contains("diễn") || lower.contains("có ai")
                || lower.contains("tìm")) {
            intents.add("SEARCH_EVENTS");
        }
        if (lower.contains("vé") || lower.contains("ticket")
                || lower.contains("giá") || lower.contains("price")
                || lower.contains("mua") || lower.contains("còn")) {
            intents.add("SEARCH_TICKETS");
        }
        if (lower.contains("đơn hàng") || lower.contains("order")
                || lower.contains("thanh toán") || lower.contains("ord-")) {
            intents.add("LOOKUP_ORDER");
        }

        // Nếu không trúng cái nào cụ thể liên quan DB thì tìm RAG
        if (intents.isEmpty() || lower.contains("quy định") || lower.contains("chính sách") || lower.contains("hỏi")) {
            intents.add("RAG_SEARCH");
        }
        return intents;
    }

    /**
     * Tool: Lấy dữ liệu Gợi ý Sự kiện cá nhân hóa (Agentic RAG kết nối Database)
     */
    private String executeRecommendationSearch(String message) {
        try {
            // Lấy current user ID
            Optional<String> currentUserEmail = SecurityUtil.getCurrentUserLogin();
            if (currentUserEmail.isEmpty() || currentUserEmail.get().equals("anonymousUser")) {
                return "Bạn cần đăng nhập để tôi có thể gợi ý sự kiện phù hợp nhất với sở thích của bạn nhé!";
            }

            var user = this.userRepository.findByEmail(currentUserEmail.get());
            if (user == null) {
                return "Lỗi xác thực người dùng để gợi ý.";
            }

            // Gọi RecommendationService lấy top event IDs
            List<Long> recommendedIds = recommendationService.getRecommendedEventIds(user.getId());

            // Tự động tìm cả nghệ sĩ user có nhắc đến trong câu không để query thẳng (agent behavior)
            String artistName = extractArtistName(message);
            List<Event> additionalArtistEvents = new ArrayList<>();
            if (!artistName.equals("KHONG_CO")) {
                // Lọc sự kiện đang bán chứa tên nghệ sĩ này
                additionalArtistEvents = eventRepository.findAll().stream()
                        .filter(e -> e.getEndTime() != null && e.getEndTime().isAfter(Instant.now()))
                        .filter(e -> e.isActive() && e.isPublished())
                        .filter(e -> e.getArtists() != null && e.getArtists().stream().anyMatch(a -> a.toLowerCase().contains(artistName.toLowerCase())))
                        .toList();
            }

            if (recommendedIds.isEmpty() && additionalArtistEvents.isEmpty()) {
                return "Rất tiếc, hiện tại tôi chưa có đủ dữ liệu lịch sử hoặc không tìm thấy sự kiện nào tương đồng để gợi ý cho bạn.";
            }

            // Lấy thông tin chi tiết các Event từ DB
            List<Event> recommendedEvents = recommendedIds.stream()
                    .map(id -> eventRepository.findById(id).orElse(null))
                    .filter(Objects::nonNull)
                    .toList();

            // Gộp danh sách gợi ý + nghệ sĩ (tránh trùng lặp)
            Set<Event> finalEvents = new HashSet<>(recommendedEvents);
            finalEvents.addAll(additionalArtistEvents);

            // Xây dựng Context String cho Agent LLM
            StringBuilder sb = new StringBuilder("Dưới đây là các sự kiện gợi ý tốt nhất dành cho bạn:\n");
            for (Event e : finalEvents) {
                sb.append("• [ID: ").append(e.getId()).append("] ").append(e.getName())
                        .append("\n  - Nghệ sĩ: ").append(e.getArtists() != null ? String.join(", ", e.getArtists()) : "Đang cập nhật")
                        .append("\n  - Thể loại: ").append(e.getGenre() != null ? e.getGenre().getName() : "Khác")
                        .append("\n  - Địa điểm: ").append(e.getLocation())
                        .append("\n  - Bắt đầu: ").append(e.getStartTime())
                        .append("\n\n");
            }
            return sb.toString();

        } catch (Exception e) {
            log.error(">>> TOOL RECOMMEND: {}", e.getMessage());
            return "Lỗi khi lấy dữ liệu gợi ý cá nhân hóa.";
        }
    }

    private String extractArtistName(String message) {
        String prompt = "Nhiệm vụ: Trích xuất tên ca sĩ/nghệ sĩ cụ thể từ câu hỏi.\n" +
                "Ví dụ nếu câu hỏi là 'tìm show của Sơn Tùng M-TP', in ra: Sơn Tùng M-TP\n" +
                "Nếu không có TÊN CỦA MỘT NGHỆ SĨ CỤ THỂ nào (chỉ hỏi show rap, nhạc acoustic, diễn ở đâu), hãy TUYỆT ĐỐI in ra: KHONG_CO\n" +
                "Chỉ in đúng tên nghệ sĩ, không in gì thêm.\n" +
                "Câu hỏi: " + message;

        String extractedName = this.ollamaService.chat(
                List.of(Map.of("role", "user", "content", prompt)), 0.1);
        
        extractedName = extractedName.trim().replaceAll("[\"']", "");
        if (extractedName.toLowerCase().contains("không có") || extractedName.contains("KHONG_CO") || extractedName.isBlank()) {
            return "KHONG_CO";
        }
        return extractedName;
    }

    /**
     * Tool: Tìm kiếm knowledge base (RAG)
     */
    private String executeRAGSearch(String query, List<ResChatDTO.SourceDTO> sources) {
        log.info(">>> TOOL RAG: searching for \"{}\"",
                query.substring(0, Math.min(80, query.length())));

        List<VectorStoreService.SearchResult> results = this.ragService.retrieveContext(query);
        log.info(">>> TOOL RAG: {} results found", results.size());

        if (results.isEmpty()) return "";

        StringBuilder ctx = new StringBuilder();
        for (VectorStoreService.SearchResult r : results) {
            ctx.append("[").append(r.getDocumentName())
                    .append(" | score=").append(String.format("%.3f", r.getScore()))
                    .append("]\n").append(r.getContent()).append("\n\n");

            ResChatDTO.SourceDTO src = new ResChatDTO.SourceDTO();
            src.setDocumentName(r.getDocumentName());
            src.setChunkPreview(r.getContent().length() > 200
                    ? r.getContent().substring(0, 200) + "..." : r.getContent());
            src.setScore(r.getScore());
            sources.add(src);
        }
        return ctx.toString();
    }

    /**
     * Helper: Loại bỏ dấu tiếng Việt và ký tự đặc biệt để tìm kiếm Flex Search
     */
    private String normalizeString(String str) {
        if (str == null) return "";
        // Bỏ dấu tiếng Việt
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        normalized = pattern.matcher(normalized).replaceAll("");
        // Đổi thành chữ thường và bỏ toàn bộ ký tự không phải chữ/số
        return normalized.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Tool: Tìm sự kiện trong DB
     */
    private String executeEventSearch(String message) {
        try {
            // Trích xuất tên
            String eventName = extractEventName(message);
            log.info(">>> TOOL EVENT: AI extracted event name = [{}]", eventName);

            List<Event> events = new ArrayList<>();
            
            if (!eventName.isEmpty()) {
                // 1. Thử tìm kiếm chính xác bằng SQL LIKE
                events = this.eventRepository.searchEventsByName(eventName);
                
                // 2. Nếu SQL LIKE thất bại (thường do dư thiếu dấu ngoặc, ký tự đặc biệt) -> Chuyển sang Flex Search
                if (events.isEmpty()) {
                    log.info(">>> TOOL EVENT: SQL LIKE failed, falling back to Flex Search (Java side filtering) for: [{}]", eventName);
                    String normalizedKeyword = normalizeString(eventName);
                    
                    // Lấy tất cả Event (hoặc giới hạn active) rồi lọc bằng Java
                    events = this.eventRepository.findAll().stream()
                            .filter(e -> e.isActive() && e.isPublished()) // Thêm điều kiện lọc cơ bản
                            .filter(e -> {
                                String normalizedDbName = normalizeString(e.getName());
                                // Kiểm tra xem tên DB (đã bỏ dấu) có chứa từ khóa (đã bỏ dấu) không
                                return normalizedDbName.contains(normalizedKeyword) || normalizedKeyword.contains(normalizedDbName);
                            })
                            .collect(Collectors.toList());
                }
            }

            // 3. Nếu vẫn không có tên sự kiện hoặc Flex Search không ra, thử tìm bằng Tên nghệ sĩ
            if (events.isEmpty()) {
                String artistName = extractArtistName(message);
                log.info(">>> TOOL EVENT: AI extracted artist name = [{}]", artistName);
                if (!artistName.equals("KHONG_CO") && !artistName.isBlank()) {
                    events = this.eventRepository.findEventsByArtistName(artistName);
                } else if (eventName.isEmpty()) {
                    // Nếu người dùng hỏi chung chung (VD: "Sắp tới có sự kiện gì"), lấy 5 sự kiện active mới nhất
                    events = this.eventRepository.findAll().stream()
                            .filter(e -> e.isActive() && e.isPublished())
                            .limit(5)
                            .collect(Collectors.toList());
                }
            }

            if (events.isEmpty()) return "Hiện không có sự kiện nào khớp với yêu cầu.";

            // Format
            StringBuilder sb = new StringBuilder("Danh sách sự kiện:\n");
            for (Event e : events) {
                sb.append("• [ID: ").append(e.getId()).append("] ").append(e.getName())
                        .append(" | Nghệ sĩ: ").append(e.getArtists() != null ? String.join(", ", e.getArtists()) : "Đang cập nhật")
                        .append(" | Địa điểm: ").append(e.getLocation())
                        .append(" | Thời gian: ").append(e.getStartTime())
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(">>> TOOL EVENT: {}", e.getMessage());
            return "Lỗi khi tìm sự kiện: " + e.getMessage();
        }
    }

    /**
     * Tool: Tìm thông tin vé
     */
    private String executeTicketSearch(String message) {
        try {
            // Gọi hàm sai vặt AI để lấy tên sự kiện
            String eventName = extractEventName(message);
            log.info(">>> TOOL TICKET: AI extracted event name = [{}]", eventName);

            if (eventName.isEmpty()) {
                return "Vui lòng cung cấp tên sự kiện cụ thể để tôi kiểm tra vé giúp bạn nhé.";
            }

            // Dùng tên đó gọi vào Database
            List<Ticket> tickets = this.ticketRepository.searchTicketsByEventName(eventName);
            
            // Nếu SQL LIKE không ra vé -> Tìm Event bằng Flex Search trước, rồi lấy list Ticket từ Event đó
            if (tickets.isEmpty()) {
                log.info(">>> TOOL TICKET: SQL LIKE failed, falling back to Flex Search for Event...");
                String normalizedKeyword = normalizeString(eventName);
                List<Event> matchedEvents = this.eventRepository.findAll().stream()
                            .filter(e -> e.isActive() && e.isPublished())
                            .filter(e -> normalizeString(e.getName()).contains(normalizedKeyword))
                            .collect(Collectors.toList());
                
                if (!matchedEvents.isEmpty()) {
                    // Lấy vé của sự kiện khớp đầu tiên
                    tickets = matchedEvents.get(0).getTickets();
                }
            }

            if (tickets == null || tickets.isEmpty()) {
                return "Hiện chưa có thông tin vé cho sự kiện: " + eventName;
            }

            // Đảm bảo lấy tên sự kiện chính xác từ DB
            String realEventName = tickets.get(0).getEvent().getName();

            // Format lại thông tin đưa cho AI chính
            StringBuilder sb = new StringBuilder("Thông tin vé của sự kiện " + realEventName + ":\n");
            for (Ticket t : tickets) {
                int remaining = t.getTotalQuantity() - t.getSoldQuantity();
                sb.append("• Loại vé: ").append(t.getTicketType())
                        .append(" | Giá: ").append(String.format("%,.0f VNĐ", t.getPrice()))
                        .append(" | Số lượng CÒN LẠI: ").append(remaining).append(" vé")
                        .append(" | Trạng thái: ").append(t.getTicketStatus())
                        .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(">>> TOOL TICKET: {}", e.getMessage());
            return "Lỗi khi tìm vé.";
        }
    }

    /**
     * Tool: Tra cứu đơn hàng
     */
    private String executeOrderLookup(String message) {
        try {
            // tìm mã đơn trong câu hỏi
            String orderCode = Arrays.stream(message.split("\\s+"))
                    .filter(w -> w.toUpperCase().startsWith("ORD-"))
                    .findFirst()
                    .map(String::toUpperCase)
                    .orElse(null);

            if (orderCode == null) {
                return "Vui lòng cung cấp mã đơn hàng (VD: ORD-20260303-ABCD1234).";
            }

            Optional<Order> found = this.orderRepository.findAll().stream()
                    .filter(o -> orderCode.equals(o.getOrderCode()))
                    .findFirst();

            if (found.isEmpty()) return "Không tìm thấy đơn hàng: " + orderCode;

            Order order = found.get();
            return String.format(
                    "Đơn hàng: %s\n• Trạng thái: %s\n• Tổng tiền: %,.0f VNĐ\n• Ngày tạo: %s\n• Thanh toán: %s",
                    order.getOrderCode(),
                    order.getOrderStatus(),
                    order.getTotalAmount(),
                    order.getCreatedAt(),
                    order.getPaidAt() != null ? order.getPaidAt() : "Chưa thanh toán"
            );
        } catch (Exception e) {
            log.error(">>> TOOL ORDER: {}", e.getMessage());
            return "Lỗi khi tra cứu đơn hàng.";
        }
    }

    private String generateAnswer(String userMessage, String toolContext,
                                  List<ResChatDTO.SourceDTO> sources,
                                  List<Map<String, Object>> history) {
        List<Map<String, Object>> messages = new ArrayList<>();

        // system prompt
        messages.add(buildSystemMessage());

        // conversation history (giữ ngữ cảnh)
        int limit = Math.min(history.size(), 10);
        for (int i = history.size() - limit; i < history.size(); i++) {
            messages.add(history.get(i));
        }

        // user prompt kèm context
        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append(userMessage);

        if (toolContext != null && !toolContext.isEmpty()) {
            userPrompt.append("\n\n--- DỮ LIỆU THAM KHẢO TỪ HỆ THỐNG ---\n").append(toolContext);
            userPrompt.append("\n--- HẾT DỮ LIỆU ---\n");
            userPrompt.append("\nNhiệm vụ của bạn là tổng hợp các dữ liệu trên thành một câu trả lời tự nhiên nhất. Nếu là dữ liệu gợi ý, hãy giới thiệu thật hấp dẫn dựa trên danh sách nghệ sĩ/thể loại. Trả lời bằng tiếng Việt.");
        }

        messages.add(Map.of("role", "user", "content", userPrompt.toString()));

        return this.ollamaService.chat(messages, temperature);
    }

    private Map<String, Object> buildSystemMessage() {
        String systemPrompt = """
                Bạn là nhân viên tư vấn bán vé chuyên nghiệp và thân thiện của nền tảng EvtGo.

                QUY TẮC TRẢ LỜI NGHIÊM NGẶT:
                1. Trả lời thẳng vào vấn đề. TUYỆT ĐỐI KHÔNG mở bài bằng các câu máy móc như "Dựa trên dữ liệu tham khảo...", "Cảm ơn bạn đã cung cấp...".
                2. Đọc CHÍNH XÁC số lượng vé và giá tiền từ DỮ LIỆU THAM KHẢO. Cấm tự bịa số liệu (như "1 vé", "2 vé") nếu dữ liệu không ghi.
                3. Trả lời bằng tiếng Việt tự nhiên, giống người thật đang chat.
                4. Nếu DỮ LIỆU THAM KHẢO báo không có thông tin, hãy nói xin lỗi khách: "Dạ, hiện em chưa tìm thấy thông tin vé cho sự kiện này ạ.
                5. Bạn là một trợ lý ảo hoạt động theo từng phiên (session-based). Bạn CHỈ ĐƯỢC PHÉP sử dụng thông tin từ lịch sử trò chuyện của phiên hiện tại.
                6. Khi gợi ý sự kiện (Recommendation), hãy nói cho người dùng biết tại sao bạn gợi ý sự kiện đó (Ví dụ: 'Dạ, vì trước đây anh/chị từng đi xem Binz, nên em xin phép gợi ý sự kiện này có nghệ sĩ cùng dòng nhạc...').
                7. RẤT QUAN TRỌNG: Khi giới thiệu MỘT SỰ KIỆN BẤT KỲ, bắt buộc phải trả về LIÊN KẾT ĐẾN SỰ KIỆN ĐÓ bằng định dạng Markdown là: [Link đến sự kiện](http://localhost:4173/events/{ID}). Hãy thay {ID} bằng con số nằm ở [ID: ...] trong DỮ LIỆU THAM KHẢO. Ví dụ nếu dữ liệu là "[ID: 5] Đêm Canh Tư", hãy in ra "[Đêm Canh Tư](/events/5)".
                """;
        return Map.of("role", "system", "content", systemPrompt);
    }

    /**
     * Load lịch sử hội thoại từ DB
     * Lọc theo sessionId + user email để tách biệt giữa các tài khoản
     */
    private List<Map<String, Object>> loadHistory(String sessionId) {
        String currentUser = SecurityUtil.getCurrentUserLogin().orElse("anonymous");
        List<ChatMessage> msgs = this.chatMessageRepository
                .findTop20BySessionIdAndCreatedByOrderByCreatedAtDesc(sessionId, currentUser);
        Collections.reverse(msgs); // đảo lại cho đúng thứ tự thời gian

        return msgs.stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("role", m.getRole());
                    map.put("content", m.getContent());
                    return map;
                })
                .collect(Collectors.toList());
    }

    private void saveMessage(String sessionId, String role, String content,
                             boolean hasImage, String toolUsed, int iteration) {
        ChatMessage msg = new ChatMessage();
        msg.setSessionId(sessionId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setHasImage(hasImage);
        msg.setToolUsed(toolUsed);
        msg.setIterationCount(iteration);
        this.chatMessageRepository.save(msg);
    }

    private ResChatDTO buildResponse(String answer, String sessionId, int iterations,
                                     List<String> tools, List<ResChatDTO.SourceDTO> sources) {
        ResChatDTO res = new ResChatDTO();
        res.setAnswer(answer);
        res.setSessionId(sessionId);
        res.setTotalIterations(iterations);
        res.setToolsUsed(tools);
        res.setSources(sources);
        res.setCreatedAt(Instant.now());
        return res;
    }

    private boolean isRelevant(String text, String query) {
        if (text == null) return false;
        // Tách câu hỏi thành các từ, nếu từ nào dài > 4 ký tự mà có trong tên sự kiện thì coi như liên quan
        String[] words = query.toLowerCase().split("\\s+");
        for (String word : words) {
            if (word.length() > 4 && text.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    private String extractEventName(String message) {
        String prompt = "Nhiệm vụ: Trích xuất tên sự kiện từ câu hỏi.\n" +
                "Chỉ in ra đúng tên sự kiện. TUYỆT ĐỐI KHÔNG in thêm bất kỳ chữ nào khác.\n" +
                "Nếu câu hỏi không nhắc đến một sự kiện cụ thể nào (ví dụ chỉ hỏi tìm show, tìm sự kiện nhạc rap, v.v.), in ra: KHONG_CO\n" +
                "Câu hỏi: " + message;

        String extractedName = this.ollamaService.chat(
                List.of(Map.of("role", "user", "content", prompt)), 0.1);

        extractedName = extractedName.trim().replaceAll("[\"']", "");
        extractedName = extractedName.replaceFirst("(?i)^tên sự kiện( là|:)?\\s*", "");

        if (extractedName.contains("KHONG_CO") || extractedName.isBlank() || extractedName.contains("không có")) {
            return "";
        }

        return extractedName;
    }

    private String executeGenreSearch(String message) {
        try {
            // Lấy từ khóa thể loại sạch từ AI
            String genreKeyword = extractGenreName(message);
            if (genreKeyword.equals("KHONG_CO")) return "Tôi chưa rõ bạn đang tìm thể loại nào.";

            // Tìm danh sách ID thể loại từ bảng Genre
            // Trong bước 2 của executeGenreSearch
            List<Long> genreIds = this.genreRepository.findAll().stream()
                    .filter(g -> {
                        String name = g.getName().toLowerCase();
                        // Kiểm tra chứa từ khóa hoặc từ khóa chứa tên (để bao quát sai sót nhỏ)
                        return name.contains(genreKeyword) || genreKeyword.contains(name);
                    })
                    .map(Genre::getId)
                    .collect(Collectors.toList());

            if (genreIds.isEmpty()) {
                return "Hiện hệ thống chưa có thông tin về thể loại: " + genreKeyword;
            }

            // Gọi Repository để lấy Event (Tối ưu hơn findAll)
            // Lưu ý: Bạn cần thêm phương thức findByGenreIdIn vào EventRepository trước
            List<Event> events = this.eventRepository.findByGenreIdIn(genreIds);

            if (events.isEmpty()) {
                return "Rất tiếc, hiện chưa có sự kiện nào đang mở thuộc thể loại này.";
            }

            // Trả về tối đa 5 kết quả cho AI
            StringBuilder sb = new StringBuilder("Tìm thấy các sự kiện thuộc thể loại " + genreKeyword + ":\n");
            events.stream().limit(5).forEach(e -> {
                sb.append("- [ID: ").append(e.getId()).append("] ").append(e.getName())
                        .append(" (Địa điểm: ").append(e.getLocation())
                        .append(" | Bắt đầu: ").append(e.getStartTime()).append(")\n");
            });

            return sb.toString();

        } catch (Exception e) {
            log.error(">>> ERR GENRE SEARCH: {}", e.getMessage());
            return "Lỗi khi truy vấn thể loại sự kiện.";
        }
    }

    private String extractGenreName(String message) {
        String prompt = "Nhiệm vụ: Trích xuất DUY NHẤT tên thể loại (VD: nhạc sống, thể thao, kịch) từ câu hỏi.\n" +
                "Bỏ qua các từ như 'tìm kiếm', 'xem', 'có...không'.\n" +
                "Nếu là 'nhạc sống' hoặc 'ca nhạc', hãy trả về: nhạc sống\n" +
                "Chỉ trả về từ khóa, không giải thích.\n" +
                "Nếu không tìm thấy thể loại, trả lời: KHONG_CO\n" +
                "Câu hỏi: " + message;

        String extracted = this.ollamaService.chat(List.of(Map.of("role", "user", "content", prompt)), 0.1).trim().toLowerCase();
        extracted = extracted.replaceAll("[\"']", "");
        return extracted;
    }
}

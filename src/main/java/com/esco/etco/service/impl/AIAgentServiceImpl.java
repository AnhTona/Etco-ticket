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

import java.time.Instant;
import java.util.*;
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

    public AIAgentServiceImpl(OllamaService ollamaService,
                              RAGService ragService,
                              ChatMessageRepository chatMessageRepository,
                              EventRepository eventRepository,
                              TicketRepository ticketRepository,
                              OrderRepository orderRepository,
                              GenreRepository genreRepository) {
        this.ollamaService = ollamaService;
        this.ragService = ragService;
        this.chatMessageRepository = chatMessageRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public ResChatDTO chat(ReqChatDTO dto) throws IdInvalidException {
        String sessionId = dto.getSessionId() != null ? dto.getSessionId() : UUID.randomUUID().toString();
        saveMessage(sessionId, "user", dto.getMessage(), false, null, 0);

        List<String> toolsUsed = new ArrayList<>();
        List<ResChatDTO.SourceDTO> sources = new ArrayList<>();
        StringBuilder allContext = new StringBuilder();

        // Luôn chạy RAG Search cho kiến thức tĩnh (Chính sách, FAQ)
        String ragResult = executeRAGSearch(dto.getMessage(), sources);
        if (!ragResult.isEmpty()) {
            toolsUsed.add("RAG_SEARCH");
            allContext.append("=== KIẾN THỨC HỆ THỐNG ===\n").append(ragResult).append("\n");
        }

        List<String> intents = detectIntent(dto.getMessage());

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

        if (lower.contains("thể loại") || lower.contains("genre") || lower.contains("loại hình")
                || lower.contains("ca nhạc") || lower.contains("hội thảo") || lower.contains("triển lãm")) {
            intents.add("SEARCH_BY_GENRE");
        }

        if (lower.contains("sự kiện") || lower.contains("event")
                || lower.contains("show") || lower.contains("concert")
                || lower.contains("lịch") || lower.contains("diễn ra")) {
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

        // Nếu không trúng cái nào thì mặc định tìm RAG
        if (intents.isEmpty()) {
            intents.add("RAG_SEARCH");
        }
        return intents;
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
     * Tool: Tìm sự kiện trong DB
     */
    private String executeEventSearch(String message) {
        try {
            // Trích xuất tên
            String eventName = extractEventName(message);
            log.info(">>> TOOL EVENT: AI extracted event name = [{}]", eventName);

            List<Event> events;
            if (eventName.isEmpty()) {
                // Nếu người dùng hỏi chung chung (VD: "Sắp tới có sự kiện gì"), lấy 5 sự kiện active mới nhất
                events = this.eventRepository.findAll().stream()
                        .filter(e -> e.isActive() && e.isPublished())
                        .limit(5)
                        .collect(Collectors.toList());
            } else {
                // Nếu hỏi tên cụ thể, gọi DB tìm theo tên
                events = this.eventRepository.searchEventsByName(eventName);
            }

            if (events.isEmpty()) return "Hiện không có sự kiện nào khớp với yêu cầu.";

            // Format
            StringBuilder sb = new StringBuilder("Danh sách sự kiện:\n");
            for (Event e : events) {
                sb.append("• ").append(e.getName())
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

            if (tickets.isEmpty()) {
                return "Hiện chưa có thông tin vé cho sự kiện: " + eventName;
            }

            // Format lại thông tin đưa cho AI chính
            StringBuilder sb = new StringBuilder("Thông tin vé của sự kiện " + eventName + ":\n");
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
            userPrompt.append("\n\n--- DỮ LIỆU THAM KHẢO ---\n").append(toolContext);
            userPrompt.append("\n--- HẾT DỮ LIỆU ---\n");
            userPrompt.append("\nTrả lời dựa trên dữ liệu tham khảo phía trên. ");
            userPrompt.append("Nếu dữ liệu không đủ, nói rõ. Trả lời bằng tiếng Việt.");
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
                5. Bạn là một trợ lý ảo hoạt động theo từng phiên (session-based). Bạn CHỈ ĐƯỢC PHÉP sử dụng thông tin từ lịch sử trò chuyện của phiên hiện tại. Tuyệt đối không được sử dụng, nhắc lại hoặc suy luận dựa trên thông tin từ các người dùng hoặc phiên làm việc khác mà bạn đã xử lý trước đó. Mỗi yêu cầu mới từ người dùng phải được coi là một ngữ cảnh độc lập trừ khi có dữ liệu lịch sử cụ thể của chính người dùng đó được cung cấp.
                6. Khi người dùng hỏi thể loại sự kiện nào hãy tìm trong events sẽ có genres_id và dựa vào đó để tìm.
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
                "Chỉ in ra đúng tên sự kiện,kèm giá vé và số lượng vé, TUYỆT ĐỐI KHÔNG in thêm bất kỳ chữ nào khác.\n" +
                "Nếu không có tên sự kiện, in ra: KHONG_CO\n" +
                "Câu hỏi: " + message;

        String extractedName = this.ollamaService.chat(
                List.of(Map.of("role", "user", "content", prompt)), 0.1);

        extractedName = extractedName.trim().replaceAll("[\"']", "");
        extractedName = extractedName.replaceFirst("(?i)^tên sự kiện( là|:)?\\s*", "");

        // NẾU AI THẤT BẠI HOẶC TRẢ VỀ RỖNG -> TỰ ĐỘNG XỬ LÝ CHUỖI
        if (extractedName.contains("KHONG_CO") || extractedName.isBlank()) {
            // Xóa các từ để hỏi phổ biến để biến câu hỏi thành từ khóa
            String fallback = message.replaceAll("(?i)(còn bao nhiêu vé|thì sao|thông tin|có|không|về|sự kiện|cho tôi biết|vé|giá|bao nhiêu|ở đâu|khi nào)", "");
            fallback = fallback.replaceAll("[,.?!]", ""); // Xóa dấu câu
            return fallback.trim();
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
                sb.append("- ").append(e.getName())
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
                "Câu hỏi: " + message;

        return this.ollamaService.chat(List.of(Map.of("role", "user", "content", prompt)), 0.1).trim().toLowerCase();
    }
}
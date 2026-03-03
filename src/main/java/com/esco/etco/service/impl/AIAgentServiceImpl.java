package com.esco.etco.service.impl;

import com.esco.etco.entity.ChatMessage;
import com.esco.etco.entity.Event;
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

    public AIAgentServiceImpl(OllamaService ollamaService,
                              RAGService ragService,
                              ChatMessageRepository chatMessageRepository,
                              EventRepository eventRepository,
                              TicketRepository ticketRepository,
                              OrderRepository orderRepository) {
        this.ollamaService = ollamaService;
        this.ragService = ragService;
        this.chatMessageRepository = chatMessageRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public ResChatDTO chat(ReqChatDTO dto) throws IdInvalidException {
        String sessionId = dto.getSessionId() != null
                ? dto.getSessionId()
                : UUID.randomUUID().toString();

        log.info(">>> AGENT START: session={}, message=\"{}\"", sessionId, dto.getMessage());

        // luu tin nhan user
        saveMessage(sessionId, "user", dto.getMessage(), false, null, 0);

        List<String> toolsUsed = new ArrayList<>();
        List<ResChatDTO.SourceDTO> sources = new ArrayList<>();
        StringBuilder allContext = new StringBuilder();

        // RAG Search (knowledge base) — LUON chay
        String ragResult = executeRAGSearch(dto.getMessage(), sources);
        if (!ragResult.isEmpty()) {
            toolsUsed.add("RAG_SEARCH");
            allContext.append("=== TU TAI LIEU (Knowledge Base) ===\n");
            allContext.append(ragResult).append("\n");
        }
        log.info(">>> AGENT: RAG returned {} chars, {} sources",
                ragResult.length(), sources.size());

        // Detect intent va goi tool phu hop
        String intent = detectIntent(dto.getMessage());
        log.info(">>> AGENT: intent={}", intent);

        if (intent.equals("SEARCH_EVENTS") || intent.equals("RAG_SEARCH")) {
            // Luon them events context
            String eventResult = executeEventSearch(dto.getMessage());
            if (!eventResult.startsWith("Loi") && !eventResult.startsWith("Hien khong")) {
                toolsUsed.add("SEARCH_EVENTS");
                allContext.append("=== SU KIEN TRONG HE THONG ===\n");
                allContext.append(eventResult).append("\n");
            }
        }

        if (intent.equals("SEARCH_TICKETS")) {
            toolsUsed.add("SEARCH_TICKETS");
            String ticketResult = executeTicketSearch(dto.getMessage());
            allContext.append("=== THONG TIN VE ===\n");
            allContext.append(ticketResult).append("\n");
        }

        if (intent.equals("LOOKUP_ORDER")) {
            toolsUsed.add("LOOKUP_ORDER");
            String orderResult = executeOrderLookup(dto.getMessage());
            allContext.append("=== DON HANG ===\n");
            allContext.append(orderResult).append("\n");
        }

        log.info(">>> AGENT: total context {} chars, tools={}", allContext.length(), toolsUsed);

        String answer = generateAnswer(
                dto.getMessage(),
                allContext.toString(),
                sources,
                loadHistory(sessionId)
        );

        // luu cau tra loi
        saveMessage(sessionId, "assistant", answer, false,
                String.join(",", toolsUsed), 1);

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

        // convert ảnh → base64
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

    /**
     * Phát hiện ý định từ câu hỏi
     */
    private String detectIntent(String message) {
        String lower = message.toLowerCase();

        if (lower.contains("sự kiện") || lower.contains("event")
                || lower.contains("show") || lower.contains("concert")
                || lower.contains("lịch") || lower.contains("diễn ra")) {
            return "SEARCH_EVENTS";
        }
        if (lower.contains("vé") || lower.contains("ticket")
                || lower.contains("giá") || lower.contains("price")
                || lower.contains("mua") || lower.contains("còn")) {
            return "SEARCH_TICKETS";
        }
        if (lower.contains("đơn hàng") || lower.contains("order")
                || lower.contains("thanh toán") || lower.contains("ORD-")) {
            return "LOOKUP_ORDER";
        }
        // mặc định: tìm trong knowledge base
        return "RAG_SEARCH";
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
            List<Event> allEvents = this.eventRepository.findAll();
            log.info(">>> TOOL EVENT: total events in DB = {}", allEvents.size());

            // Uu tien active+published, nhung neu khong co thi lay tat ca
            List<Event> events = allEvents.stream()
                    .filter(e -> e.isActive() && e.isPublished())
                    .collect(Collectors.toList());

            if (events.isEmpty()) {
                log.info(">>> TOOL EVENT: no active+published events, showing all");
                events = allEvents;
            }

            if (events.isEmpty()) return "Hien khong co su kien nao trong he thong.";

            StringBuilder sb = new StringBuilder("Danh sach su kien (" + events.size() + "):\n");
            for (Event e : events) {
                sb.append("• ").append(e.getName());
                if (e.getLocation() != null) sb.append(" | Dia diem: ").append(e.getLocation());
                if (e.getStartTime() != null) sb.append(" | Thoi gian: ").append(e.getStartTime());
                if (e.getEndTime() != null) sb.append(" -> ").append(e.getEndTime());
                sb.append(" | Active=").append(e.isActive())
                  .append(", Published=").append(e.isPublished());
                if (e.getDescription() != null && !e.getDescription().isBlank()) {
                    String desc = e.getDescription().length() > 200
                            ? e.getDescription().substring(0, 200) + "..."
                            : e.getDescription();
                    sb.append("\n  Mo ta: ").append(desc);
                }
                sb.append("\n");
            }
            log.info(">>> TOOL EVENT: returning {} events", events.size());
            return sb.toString();
        } catch (Exception e) {
            log.error(">>> TOOL EVENT: {}", e.getMessage());
            return "Loi khi tim su kien: " + e.getMessage();
        }
    }

    /**
     * Tool: Tìm thông tin vé
     */
    private String executeTicketSearch(String message) {
        try {
            List<Ticket> tickets = this.ticketRepository.findAll();
            if (tickets.isEmpty()) return "Chưa có thông tin vé.";

            StringBuilder sb = new StringBuilder("Thông tin vé:\n");
            for (Ticket t : tickets) {
                String eventName = t.getEvent() != null ? t.getEvent().getName() : "N/A";
                int remaining = t.getTotalQuantity() - t.getSoldQuantity();
                sb.append("• Sự kiện: ").append(eventName)
                        .append(" | Loại: ").append(t.getTicketType())
                        .append(" | Giá: ").append(String.format("%,.0f VNĐ", t.getPrice()))
                        .append(" | Còn: ").append(remaining).append(" vé")
                        .append(" | ").append(t.getTicketStatus())
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
                Bạn là trợ lý AI của EvtGo - nền tảng bán vé sự kiện.

                Khả năng:
                1. Trả lời về sự kiện, vé, đơn hàng (từ database hệ thống)
                2. Tìm kiếm thông tin từ tài liệu đã upload (knowledge base)
                3. Phân tích ảnh (poster sự kiện, vé, QR code, hóa đơn)
                4. Nhớ ngữ cảnh hội thoại trước đó

                Quy tắc:
                - Trả lời bằng tiếng Việt, thân thiện, chính xác
                - Giá tiền format: 500,000 VNĐ
                - Nếu không biết → nói rõ "Tôi không có thông tin"
                - Ưu tiên dùng dữ liệu hệ thống khi có
                - Trả lời đúng trọng tâm câu hỏi câu trả lời lang mang như giải thích việc kiếm dữ liệu ở đâu
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
}
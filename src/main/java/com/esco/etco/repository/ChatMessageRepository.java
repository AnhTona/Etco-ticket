package com.esco.etco.repository;

import com.esco.etco.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // lấy lịch sử chat theo session, sắp xếp theo thời gian
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    // lấy N tin nhắn gần nhất (dùng cho conversation memory)
    List<ChatMessage> findTop20BySessionIdOrderByCreatedAtDesc(String sessionId);

    // lấy N tin nhắn gần nhất theo session + user (tách biệt giữa các user)
    List<ChatMessage> findTop20BySessionIdAndCreatedByOrderByCreatedAtDesc(String sessionId, String createdBy);
}
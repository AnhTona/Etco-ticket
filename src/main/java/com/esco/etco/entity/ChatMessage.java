package com.esco.etco.entity;

import com.esco.etco.util.SecurityUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "chat_message")
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String sessionId;

    private String role;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    private boolean hasImage;
    private String toolUsed;
    private int iterationCount;

    private Instant createdAt;
    private String createdBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "anonymous";
        this.createdAt = Instant.now();
    }


}

package com.esco.etco.entity;

import com.esco.etco.util.SecurityUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fileName;

    // tên file lưu trên ổ đĩa (qua FileService)
    private String storedFileName;

    private String fileType;

    private long fileSize;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String rawContent;

    private int totalChunks;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DocumentChunk> chunks;

    private Instant createdAt;
    private String createdBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "system";
        this.createdAt = Instant.now();
    }
}
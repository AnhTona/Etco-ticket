package com.esco.etco.service.impl;

import com.esco.etco.entity.DocumentChunk;
import com.esco.etco.repository.DocumentChunkRepository;
import com.esco.etco.service.OllamaService;
import com.esco.etco.service.VectorStoreService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmbeddingAsyncService {

    @Value("${etco.rag.embedding-batch-size:20}")
    private int batchSize;

    private final DocumentChunkRepository documentChunkRepository;
    private final OllamaService ollamaService;
    private final VectorStoreService vectorStoreService;
    private final EntityManager entityManager;

    public EmbeddingAsyncService(DocumentChunkRepository documentChunkRepository,
                                 OllamaService ollamaService,
                                 VectorStoreService vectorStoreService,
                                 EntityManager entityManager) {
        this.documentChunkRepository = documentChunkRepository;
        this.ollamaService = ollamaService;
        this.vectorStoreService = vectorStoreService;
        this.entityManager = entityManager;
    }

    /**
     * Embedding chạy NỀN — gửi BATCH thay vì từng chunk
     *
     * TRƯỚC: 280 chunks → 280 HTTP requests → chậm (30-60 giây/chunk)
     * SAU:   280 chunks → 14 batch × 20 chunks → 14 HTTP requests → GPU song song
     */
    @Async
    @Transactional
    public void embedChunks(long documentId) {
        log.info(">>> EMBED START: documentId={}, batchSize={}", documentId, batchSize);
        long startTime = System.currentTimeMillis();

        List<DocumentChunk> allChunks = this.documentChunkRepository.findByDocumentId(documentId);

        // Loc chunks chua co embedding
        List<DocumentChunk> pendingChunks = new ArrayList<>();
        for (DocumentChunk chunk : allChunks) {
            if (chunk.getEmbedding() == null) {
                pendingChunks.add(chunk);
            }
        }
        // Giai phong list cu
        allChunks = null;

        if (pendingChunks.isEmpty()) {
            log.info(">>> EMBED: all chunks already have embeddings");
            return;
        }

        int total = pendingChunks.size();
        int totalBatches = (int) Math.ceil((double) total / batchSize);
        log.info(">>> EMBED: {} chunks -> {} batches", total, totalBatches);

        int embeddedCount = 0;

        for (int batchIdx = 0; batchIdx < totalBatches; batchIdx++) {
            int from = batchIdx * batchSize;
            int to = Math.min(from + batchSize, total);
            List<DocumentChunk> batch = pendingChunks.subList(from, to);

            // Thu thap text
            List<String> texts = new ArrayList<>(batch.size());
            for (DocumentChunk chunk : batch) {
                texts.add(chunk.getContent());
            }

            try {
                // 1 HTTP request cho ca batch -> GPU xu ly song song
                List<double[]> embeddings = this.ollamaService.getEmbeddingBatch(texts);

                // Luu truc tiep vao entities -> saveAll 1 lan (KHONG query lai DB)
                this.vectorStoreService.storeBatchEmbeddings(batch, embeddings);
                embeddedCount += embeddings.size();

            } catch (Exception e) {
                log.warn(">>> EMBED: batch {}/{} failed: {}", batchIdx + 1, totalBatches, e.getMessage());
            }

            // QUAN TRONG: flush + clear JPA cache SAU MOI BATCH -> giai phong RAM
            entityManager.flush();
            entityManager.clear();

            log.info(">>> EMBED: batch {}/{} done (total embedded: {})",
                    batchIdx + 1, totalBatches, embeddedCount);
        }

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        log.info(">>> EMBED DONE: {}/{} chunks, documentId={}, {}s",
                embeddedCount, total, documentId, elapsed);
    }
}
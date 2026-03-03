package com.esco.etco.service.impl;

import com.esco.etco.entity.Document;
import com.esco.etco.entity.DocumentChunk;
import com.esco.etco.entity.response.chat.ResIngestDTO;
import com.esco.etco.repository.DocumentChunkRepository;
import com.esco.etco.repository.DocumentRepository;
import com.esco.etco.service.OllamaService;
import com.esco.etco.service.RAGService;
import com.esco.etco.service.VectorStoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RAGServiceImpl implements RAGService {

    @Value("${etco.rag.chunk-size}")
    private int chunkSize;

    @Value("${etco.rag.chunk-overlap}")
    private int chunkOverlap;

    @Value("${etco.rag.top-k}")
    private int topK;

    @Value("${etco.rag.similarity-threshold}")
    private double threshold;

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final OllamaService ollamaService;
    private final VectorStoreService vectorStoreService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RAGServiceImpl(DocumentRepository documentRepository,
                          DocumentChunkRepository documentChunkRepository,
                          OllamaService ollamaService,
                          VectorStoreService vectorStoreService) {
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.ollamaService = ollamaService;
        this.vectorStoreService = vectorStoreService;
    }

    // INGEST: đọc text → chunk → embedding TỪNG CÁI MỘT → lưu DB
    // Đơn giản nhất có thể, không lưu file, không async, không batch
    @Override
    @Transactional
    public ResIngestDTO ingestDocument(MultipartFile file) throws Exception {
        logHeap("INGEST START");
        log.info(">>> INGEST: file={}, size={}KB",
                file.getOriginalFilename(), file.getSize() / 1024);

        //heap tối thiểu 512MB, nếu không → dừng với hướng dẫn rõ ràng
        Runtime rt = Runtime.getRuntime();
        long maxMB = rt.maxMemory() / 1024 / 1024;
        if (maxMB < 512) {
            throw new Exception(
                "JVM Max Heap chi co " + maxMB + "MB! Can it nhat 1024MB. "
                + "Vao Run > Edit Configurations > VM options: -Xms512m -Xmx4g");
        }

        //Validate
        validateFileType(file);

        //GC truoc khi lam viec nang
        System.gc();
        logHeap("AFTER GC");

        // 3. Doc text
        String text = extractText(file);
        log.info(">>> INGEST: extracted {} chars", text.length());
        logHeap("AFTER PDF PARSE");

        if (text.isBlank()) {
            throw new Exception("Khong doc duoc noi dung tu file.");
        }

        //Chunk text
        List<String> textChunks = chunkText(text);
        text = null; // giai phong
        log.info(">>> INGEST: {} chunks", textChunks.size());

        // 5. Luu Document metadata
        Document doc = new Document();
        doc.setFileName(file.getOriginalFilename());
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());
        doc.setTotalChunks(textChunks.size());
        Document savedDoc = this.documentRepository.save(doc);
        logHeap("AFTER SAVE DOC");

        //Embedding tung chunk mot
        int embeddedCount = 0;
        for (int i = 0; i < textChunks.size(); i++) {
            String chunkText = textChunks.get(i);

            DocumentChunk chunk = new DocumentChunk();
            chunk.setContent(chunkText);
            chunk.setChunkIndex(i);
            chunk.setDocument(savedDoc);

            logHeap("BEFORE EMBED chunk " + i);

            try {
                double[] emb = this.ollamaService.getEmbedding(chunkText);
                logHeap("AFTER EMBED chunk " + i + " (dim=" + emb.length + ")");

                if (emb.length > 0) {
                    chunk.setEmbedding(objectMapper.writeValueAsString(emb));
                    embeddedCount++;
                }
            } catch (Exception e) {
                log.warn(">>> INGEST: embed chunk {} failed: {}", i, e.getMessage());
            }

            this.documentChunkRepository.save(chunk);
            log.info(">>> INGEST: chunk {}/{} done", i + 1, textChunks.size());
        }

        logHeap("INGEST DONE");
        log.info(">>> INGEST DONE: embedded {}/{}", embeddedCount, textChunks.size());

        ResIngestDTO res = new ResIngestDTO();
        res.setDocumentId(savedDoc.getId());
        res.setFileName(savedDoc.getFileName());
        res.setTotalChunks(savedDoc.getTotalChunks());
        res.setMessage("Embedded " + embeddedCount + "/" + textChunks.size()
                + " chunks. MaxHeap=" + maxMB + "MB");
        res.setCreatedAt(savedDoc.getCreatedAt());
        return res;
    }

    private void logHeap(String phase) {
        Runtime rt = Runtime.getRuntime();
        long max = rt.maxMemory() / 1024 / 1024;
        long total = rt.totalMemory() / 1024 / 1024;
        long free = rt.freeMemory() / 1024 / 1024;
        long used = total - free;
        log.info(">>> HEAP [{}]: used={}MB / max={}MB (free={}MB)",
                phase, used, max, free);
    }

    // EXTRACT TEXT — đọc trực tiếp từ MultipartFile, không qua disk
    private String extractText(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase() : "";

        // PDF
        if ("application/pdf".equals(contentType) || fileName.endsWith(".pdf")) {
            byte[] bytes = file.getBytes();
            log.info(">>> PDF: reading {} KB", bytes.length / 1024);
            try (PDDocument pdf = Loader.loadPDF(bytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdf);
                log.info(">>> PDF: {} pages, {} chars",
                        pdf.getNumberOfPages(), text.length());
                return text;
            }
            // bytes sẽ được GC sau khi ra khỏi scope
        }

        // Text file
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private void validateFileType(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() != null
                ? file.getOriginalFilename().toLowerCase() : "";

        boolean isPdf = "application/pdf".equals(contentType) || fileName.endsWith(".pdf");
        boolean isText = (contentType != null && contentType.startsWith("text/"))
                || fileName.endsWith(".txt") || fileName.endsWith(".csv")
                || fileName.endsWith(".md");

        if (!isPdf && !isText) {
            throw new Exception("Chi ho tro PDF, TXT, CSV, MD. File type: " + contentType);
        }
    }

    // RETRIEVE — tìm kiếm trong knowledge base
    @Override
    public List<VectorStoreService.SearchResult> retrieveContext(String query) {
        double[] queryEmb = this.ollamaService.getEmbedding(query);
        if (queryEmb.length == 0) {
            log.error(">>> RETRIEVE: embedding query failed");
            return new ArrayList<>();
        }
        List<VectorStoreService.SearchResult> results =
                this.vectorStoreService.search(queryEmb, topK, threshold);
        log.info(">>> RETRIEVE: {} results for \"{}\"",
                results.size(), query.substring(0, Math.min(50, query.length())));
        return results;
    }

    // CHUNK TEXT
    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) return chunks;

        text = text.replaceAll("\\s+", " ").trim();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());

            if (end < text.length()) {
                int breakPos = findBreakPoint(text, start, end);
                if (breakPos > start) end = breakPos;
            }

            String chunk = text.substring(start, end).trim();
            if (!chunk.isEmpty()) chunks.add(chunk);

            // Đã đọc hết text → dừng
            if (end >= text.length()) break;

            // Lùi lại chunkOverlap ký tự để tạo overlap
            start = end - chunkOverlap;
            if (start < 0) start = 0;
        }
        return chunks;
    }

    private int findBreakPoint(String text, int start, int end) {
        for (int i = end; i > start + (end - start) / 2; i--) {
            char c = text.charAt(i - 1);
            if (c == '.' || c == '!' || c == '?' || c == '\n') return i;
        }
        for (int i = end; i > start + (end - start) / 2; i--) {
            if (text.charAt(i - 1) == ' ') return i;
        }
        return end;
    }
}
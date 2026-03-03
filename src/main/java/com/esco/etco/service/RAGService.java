package com.esco.etco.service;

import com.esco.etco.entity.response.chat.ResIngestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RAGService {

    // upload file → chunk → embedding → lưu DB
    ResIngestDTO ingestDocument(MultipartFile file) throws Exception;

    // tìm context phù hợp với câu hỏi
    List<VectorStoreService.SearchResult> retrieveContext(String query);
}
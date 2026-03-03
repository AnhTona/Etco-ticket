package com.esco.etco.service;

import com.esco.etco.entity.request.ReqChatDTO;
import com.esco.etco.entity.response.chat.ResChatDTO;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.web.multipart.MultipartFile;

public interface AIAgentService {

    // chat text (Agentic RAG)
    ResChatDTO chat(ReqChatDTO dto) throws IdInvalidException;

    // chat kèm ảnh (Multimodal Agentic RAG)
    ResChatDTO chatWithImage(String message, String sessionId, MultipartFile image) throws Exception;
}
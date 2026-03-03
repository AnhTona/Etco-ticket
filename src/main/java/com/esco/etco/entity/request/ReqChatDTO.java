package com.esco.etco.entity.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqChatDTO {
    private String message;
    private String sessionId;
    private String userEmail;
}
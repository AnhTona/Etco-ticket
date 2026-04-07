package com.esco.etco.entity.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResEventStaffDTO {
    private long id;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String userName;
    private String userEmail;
    private Instant createdAt;
    private String createdBy;
}

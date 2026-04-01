package com.esco.etco.entity.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResProducerDTO {
    private long id;
    private String producerName;
    private String bankName;
    private String bankAccountNumber;
    private String contactEmail;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}

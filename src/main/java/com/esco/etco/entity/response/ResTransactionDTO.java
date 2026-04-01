package com.esco.etco.entity.response;

import com.esco.etco.util.constant.TransactionStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResTransactionDTO {
    private long id;
    private Long userTicketId;
    private Double amount;
    private String paymentMethod;
    private TransactionStatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}

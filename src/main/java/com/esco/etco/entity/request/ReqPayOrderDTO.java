package com.esco.etco.entity.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqPayOrderDTO {
    private long orderId;
    // Sau này mở rộng: paymentMethod, transactionId, etc.
}
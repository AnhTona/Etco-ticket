package com.esco.etco.entity.request;

import com.esco.etco.util.constant.TransactionStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqTransactionDTO {
    @NotNull(message = "ID vé người dùng không được để trống")
    private Long userTicketId;

    @NotNull(message = "Số tiền không được để trống")
    private Double amount;

    private String paymentMethod;

    @NotNull(message = "Trạng thái không được để trống")
    private TransactionStatusEnum status;
}

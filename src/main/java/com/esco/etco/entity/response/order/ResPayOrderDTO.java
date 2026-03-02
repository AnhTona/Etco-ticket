package com.esco.etco.entity.response.order;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResPayOrderDTO {
    private long orderId;
    private String orderCode;
    private String orderStatus;
    private Instant paidAt;
    private double totalAmount;

    private List<UserTicketDTO> userTickets;

    @Getter
    @Setter
    public static class UserTicketDTO {
        private long id;
        private String qrCode;
        private String eventName;
        private String ticketType;
        private String status;
        private Instant issuedAt;
    }
}
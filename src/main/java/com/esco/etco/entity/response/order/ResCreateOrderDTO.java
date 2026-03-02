package com.esco.etco.entity.response.order;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCreateOrderDTO {
    private long id;
    private String orderCode;
    private double totalAmount;
    private String orderStatus;
    private Instant createdAt;
    private String createdBy;

    private List<OrderItemDTO> items;

    @Getter
    @Setter
    public static class OrderItemDTO {
        private long id;
        private long ticketId;
        private String ticketType;
        private String eventName;
        private int quantity;
        private double pricePerUnit;
        private double subtotal;
    }
}
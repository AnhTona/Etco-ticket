package com.esco.etco.entity.response.order;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResOrderDTO {
    private long id;
    private String orderCode;
    private double totalAmount;
    private String orderStatus;
    private Instant paidAt;
    private Instant createdAt;
    private String createdBy;
    private UserDTO user;
    private List<OrderItemDTO> items;

    @Getter
    @Setter
    public static class UserDTO {
        private long id;
        private String name;
        private String email;
    }

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
        // Bổ sung thông tin ghế
        private String seatLabel;
        private String zone;
    }
}
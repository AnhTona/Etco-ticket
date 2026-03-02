package com.esco.etco.entity.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReqOrderDTO {
    private List<OrderItemDTO> items;

    @Getter
    @Setter
    public static class OrderItemDTO {
        private long ticketId;  // Loại vé muốn mua
        private int quantity;   // Số lượng
    }
}
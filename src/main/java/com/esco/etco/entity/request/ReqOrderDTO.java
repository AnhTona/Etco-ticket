package com.esco.etco.entity.request;

import com.fasterxml.jackson.annotation.JsonProperty; // Thêm import này
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ReqOrderDTO {

    @JsonProperty("isSeated")
    private boolean isSeated;

    private List<OrderItemDTO> items;

    @Getter
    @Setter
    public static class OrderItemDTO {
        private long ticketId;
        private long seatId;
        private int quantity;
    }
}
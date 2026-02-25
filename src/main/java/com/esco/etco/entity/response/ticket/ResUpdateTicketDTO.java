package com.esco.etco.entity.response.ticket;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateTicketDTO {
    private long id;
    private int totalQuantity;
    private int soldQuantity;
    private String ticketType;
    private String ticketStatus;
    private double price;

    private String updatedBy;
    private Instant updatedAt;

    private EventTicket event;

    @Getter
    @Setter
    public static class EventTicket {
        private long id;
        private String name;
    }
}
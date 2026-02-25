package com.esco.etco.entity.response.ticket;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateTicketDTO {
    private long id;
    private int totalQuantity;
    private int soldQuantity;
    private String ticketType;
    private String ticketStatus;
    private double price;

    private String createdBy;
    private Instant createdAt;

    private EventTicket event;

    @Getter
    @Setter
    public static class EventTicket {
        private long id;
        private String name;
    }
}
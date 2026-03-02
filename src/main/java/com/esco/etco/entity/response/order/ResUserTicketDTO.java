package com.esco.etco.entity.response.order;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUserTicketDTO {
    private long id;
    private String qrCode;
    private String status;
    private Instant issuedAt;
    private Instant usedAt;

    private EventDTO event;
    private TicketDTO ticket;

    @Getter
    @Setter
    public static class EventDTO {
        private long id;
        private String name;
        private String location;
        private Instant startTime;
        private Instant endTime;
    }

    @Getter
    @Setter
    public static class TicketDTO {
        private long id;
        private String ticketType;
        private double price;
    }
}
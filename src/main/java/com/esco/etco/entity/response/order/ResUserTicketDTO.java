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
    // Bổ sung thông tin ghế để khách biết mình ngồi đâu
    private String seatLabel;
    private String zone;
    private long orderId;
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
        private String image;
    }

    @Getter
    @Setter
    public static class TicketDTO {
        private long id;
        private String ticketType;
        private double price;
    }
}
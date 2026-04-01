package com.esco.etco.entity.response;

import com.esco.etco.util.constant.UserTicketStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUserTicketDTO {
    private long id;
    private String qrCode;
    private UserTicketStatusEnum status;
    private Instant issuedAt;
    private Instant usedAt;
    private Long userId;
    private Long ticketId;
    private Long eventId;
    private Long orderId;
    private Long seatId;
}

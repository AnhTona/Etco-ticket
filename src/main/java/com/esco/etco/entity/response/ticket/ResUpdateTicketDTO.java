package com.esco.etco.entity.response.ticket;

import com.esco.etco.util.constant.EventTicketEnum;
import com.esco.etco.util.constant.TicketEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateTicketDTO {
    private long id;
    private int totalQuantity;
    private int soldQuantity;
    private EventTicketEnum ticketType;
    private TicketEnum ticketStatus;
    private Instant updatedAt;
    private String updatedBy;
}

package com.esco.etco.entity.response.ticket;

import com.esco.etco.util.constant.EventTicketEnum;
import com.esco.etco.util.constant.TicketEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateTicketDTO {
    private long id;
    private int totalQuantity;
    private int soldQuantity;
    private EventTicketEnum ticketType;
    private TicketEnum ticketStatus;
    private Instant createdAt;
    private String createdBy;
}

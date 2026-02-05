package com.esco.etco.entity;

import com.esco.etco.util.constant.EventTicketEnum;
import com.esco.etco.util.constant.TicketEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tickets")
@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Vui lòng nhập số lượng vé")
    private int totalQuantity;

    private int soldQuantity;

    @Enumerated(EnumType.STRING)
    private EventTicketEnum ticketType;

    @Enumerated(EnumType.STRING)
    private TicketEnum ticketStatus;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
}

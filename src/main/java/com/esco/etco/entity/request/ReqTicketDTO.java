package com.esco.etco.entity.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqTicketDTO {
    private int totalQuantity;
    private String ticketType;   // VIP, STANDARD
    private String ticketStatus; // PUBLISHED, SOLD_OUT, STOPPED
    private double price;
    private long eventId;
}
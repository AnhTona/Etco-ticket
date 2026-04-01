package com.esco.etco.entity.response;

import com.esco.etco.util.constant.SeatStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResSeatDTO {
    private long id;
    private String seatLabel;
    private String zone;
    private double price;
    private SeatStatusEnum status;
    private Long eventId;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}

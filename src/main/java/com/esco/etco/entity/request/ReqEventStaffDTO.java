package com.esco.etco.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqEventStaffDTO {
    @NotNull(message = "Event ID không được để trống")
    private Long eventId;

    @NotNull(message = "User ID không được để trống")
    private Long userId;
}

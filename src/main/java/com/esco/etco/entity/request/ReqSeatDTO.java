package com.esco.etco.entity.request;

import com.esco.etco.util.constant.SeatStatusEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSeatDTO {
    @NotBlank(message = "Nhãn ghế không được để trống")
    private String seatLabel;

    @NotBlank(message = "Khu vực ghế không được để trống")
    private String zone;

    @NotNull(message = "Giá ghế không được để trống")
    @DecimalMin(value = "1000.0", message = "Giá ghế phải lớn hơn 1000")
    private double price;

    @NotNull(message = "Trạng thái ghế không được để trống")
    private SeatStatusEnum status;

    @NotNull(message = "ID sự kiện không được để trống")
    private Long eventId;
}

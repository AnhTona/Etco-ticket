package com.esco.etco.entity.request;

import com.esco.etco.util.constant.UserTicketStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUserTicketDTO {
    @NotBlank(message = "Mã QR không được để trống")
    private String qrCode;

    @NotNull(message = "Trạng thái không được để trống")
    private UserTicketStatusEnum status;

    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;

    @NotNull(message = "ID vé không được để trống")
    private Long ticketId;

    @NotNull(message = "ID sự kiện không được để trống")
    private Long eventId;

    @NotNull(message = "ID đơn hàng không được để trống")
    private Long orderId;

    private Long seatId;
}

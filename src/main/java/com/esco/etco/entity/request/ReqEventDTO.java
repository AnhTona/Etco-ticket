package com.esco.etco.entity.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

// class này nhận date và time dạng string để dễ thao tác
@Getter
@Setter
public class ReqEventDTO {
    @NotBlank(message = "Vui lòng nhập tên sự kiện")
    private String name;

    @NotBlank(message = "Vui lòng nhập miêu tả")
    private String description;

    @NotBlank(message = "Vui Lòng nhập số giấy phép")
    private String permitNumber;

    @NotBlank(message = "Vui lòng nhập ngày cấp")
    private Instant permitIssuedAt;

    @NotBlank(message = "Vui lòng nhập nơi cấp")
    private String permitIssuedBy;

    @NotBlank(message = "vui lòng nhập địa chỉ")
    private String location;

    @NotBlank(message = "vui lòng nhập ngày bắt đầu")
    private String startDate; // 2026-02-10

    @NotBlank(message = "vui lòng nhập giờ bắt đầu")
    private String startTime; // 18:30

    @NotBlank(message = "vui lòng nhập ngày kế thúc")
    private String endDate;

    @NotBlank(message = "vui lòng nhập giờ kết thúc")
    private String endTime;
}

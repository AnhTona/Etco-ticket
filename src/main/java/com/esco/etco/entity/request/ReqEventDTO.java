package com.esco.etco.entity.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Vui lòng nhập số giấy phép")
    private String permitNumber;

    @NotBlank(message = "Vui lòng nhập ngày cấp giấy phép")
    private String permitIssuedAt;

    @NotBlank(message = "Vui lòng nhập nơi cấp giấy phép")
    private String permitIssuedBy;

    @NotBlank(message = "Vui lòng nhập địa chỉ tổ chức")
    private String location;

    @NotBlank(message = "Vui lòng nhập ngày bắt đầu")
    private String startDate; // Ví dụ: 2026-02-10

    @NotBlank(message = "Vui lòng nhập giờ bắt đầu")
    private String startTime; // Ví dụ: 18:30

    @NotBlank(message = "Vui lòng nhập ngày kết thúc")
    private String endDate;

    @NotBlank(message = "Vui lòng nhập giờ kết thúc")
    private String endTime;

    @NotNull(message = "Vui lòng chọn thể loại sự kiện")
    private Long genreId;
}
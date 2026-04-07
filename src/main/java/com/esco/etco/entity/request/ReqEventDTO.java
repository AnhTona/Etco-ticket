package com.esco.etco.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private String startDate;

    @NotBlank(message = "Vui lòng nhập giờ bắt đầu")
    private String startTime;

    @NotBlank(message = "Vui lòng nhập ngày kết thúc")
    private String endDate;

    @NotBlank(message = "Vui lòng nhập giờ kết thúc")
    private String endTime;

    @NotNull(message = "Vui lòng chọn thể loại sự kiện")
    private Long genreId;

    private Long producerId;

    private List<String> artists;
    private ProducerDTO producer;

    @Getter
    @Setter
    public static class ProducerDTO {
        private String producerName;
        private String contactEmail;
        private String bankName;
        private String bankAccountNumber;
    }
}
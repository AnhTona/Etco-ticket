package com.esco.etco.entity.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqProducerDTO {
    @NotBlank(message = "Tên nhà sản xuất không được để trống")
    private String producerName;

    @NotBlank(message = "Tên ngân hàng không được để trống")
    private String bankName;

    @NotBlank(message = "Số tài khoản không được để trống")
    private String bankAccountNumber;

    private String contactEmail;
}

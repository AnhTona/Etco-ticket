package com.esco.etco.entity.response.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResEventDTO {
    private long id;
    private String name;
    private String description;
    private String location;

    private boolean isActive;
    private boolean isPublished;

    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;

    private String createdBy;
    private Instant createdAt;

    // Các trường về giấy phép (Permit)
    private String permitNumber;
    private Instant permitIssuedAt;
    private String permitIssuedBy;

    // Thông tin Thể loại (Genre)
    private GenreDTO genre;

    // THÊM: Thông tin Nhà tổ chức (Producer) từ Step 4
    private ProducerDTO producer;

    // Danh sách ảnh của event
    private List<ImageDTO> images;

    // Danh sách nghệ sĩ
    private List<String> artists;

    @Getter
    @Setter
    public static class ImageDTO {
        private long id;
        private String url;
        private boolean isCover;
    }

    @Getter
    @Setter
    public static class GenreDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class ProducerDTO {
        private String producerName;
        private String contactEmail;
        private String bankName;
        private String bankAccountNumber;
    }
}
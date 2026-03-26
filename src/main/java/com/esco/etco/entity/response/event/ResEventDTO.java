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

    // Hợp nhất: Thêm các trường về giấy phép (Permit)
    private String permitNumber;
    private Instant permitIssuedAt;
    private String permitIssuedBy;

    // Hợp nhất: Thêm thông tin Genre
    private GenreDTO genre;

    // Hợp nhất: Danh sách ảnh của event (đầy đủ các trường)
    private List<ImageDTO> images;

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
}
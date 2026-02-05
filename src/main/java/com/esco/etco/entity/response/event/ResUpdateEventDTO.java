package com.esco.etco.entity.response.event;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateEventDTO {
    private long id;
    private String name;
    private String description;
    private String location;
    private List<String> urlImage;

    private boolean isActive;
    private boolean isPublished;

    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String updateBy;
    private Instant updateAt;
}

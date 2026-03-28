package com.esco.etco.entity.response.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationDTO {
    private long id;
    private String name;
    private String location;
    private String imageUrl;
}
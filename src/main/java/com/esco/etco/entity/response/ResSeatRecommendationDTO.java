package com.esco.etco.entity.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResSeatRecommendationDTO {
    private boolean hasRecommendation;
    private boolean isWarning;
    private String message;
    private List<ResSeatDTO> data;
}

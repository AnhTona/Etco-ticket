package com.esco.etco.entity.response.genre;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateGenreDTO {
    private long id;
    private String name;

    private String createdBy;
    private Instant createdAt;
}

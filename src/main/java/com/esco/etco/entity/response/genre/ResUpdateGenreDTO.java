package com.esco.etco.entity.response.genre;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateGenreDTO {
    private long id;
    private String name;

    private String updatedBy;
    private Instant updatedAt;
}

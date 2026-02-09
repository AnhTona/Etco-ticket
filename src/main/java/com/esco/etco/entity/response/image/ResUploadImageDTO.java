package com.esco.etco.entity.response.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUploadImageDTO {
    private long id;
    private String fileName;
    private boolean isCover;
    private Instant uploadedAt;
}

package com.esco.etco.controller.admin;

import com.esco.etco.entity.response.image.ResUploadImageDTO;
import com.esco.etco.service.EventImageService;
import com.esco.etco.util.annotation.ApiMessage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class EventImageController {

    private final EventImageService eventImageService;

    public EventImageController(EventImageService eventImageService) {
        this.eventImageService = eventImageService;
    }

    @PostMapping("/events/{eventId}/images")
    @ApiMessage("Upload event images")
    public ResponseEntity<List<ResUploadImageDTO>> uploadEventImages(
            @PathVariable long eventId,
            @RequestParam(name = "files") MultipartFile[] files,
            @RequestParam(name = "coverIndex", required = false) Integer coverIndex
    ) throws Exception {

        List<ResUploadImageDTO> result = this.eventImageService.uploadEventImages(eventId, files, coverIndex);
        return ResponseEntity.ok(result);
    }
}
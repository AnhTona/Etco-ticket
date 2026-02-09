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

    @DeleteMapping("/events/{eventId}/images/{imageId}")
    @ApiMessage("Delete event image")
    public ResponseEntity<Void> deleteEventImage(
            @PathVariable long eventId,
            @PathVariable long imageId
    ) throws Exception {

        this.eventImageService.deleteEventImage(eventId, imageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/events/{eventId}/images/{imageId}")
    @ApiMessage("Update event image")
    public ResponseEntity<ResUploadImageDTO> updateEventImage(
            @PathVariable long eventId,
            @PathVariable long imageId,
            @RequestParam(name = "file") MultipartFile file
    ) throws Exception {

        ResUploadImageDTO result = this.eventImageService.updateEventImage(eventId, imageId, file);
        return ResponseEntity.ok(result);
    }
}
package com.esco.etco.controller.admin;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.request.ReqEventDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.event.ResCreateEventDTO;
import com.esco.etco.entity.response.event.ResUpdateEventDTO;
import com.esco.etco.service.EventService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    @ApiMessage("Tạo sự kiện mới")
    public ResponseEntity<ResCreateEventDTO> createEvent(
            @Valid @RequestBody ReqEventDTO dto) {
        ResCreateEventDTO created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/events/{id}")
    @ApiMessage("Cập nhật sự kiện")
    public ResponseEntity<ResUpdateEventDTO> updateEvent(
            @PathVariable long id,
            @Valid @RequestBody ReqEventDTO dto) throws IdInvalidException {
        Event existing = eventService.getEventById(id);
        if (existing == null) {
            throw new IdInvalidException("Không tìm thấy sự kiện với id = " + id);
        }
        return ResponseEntity.ok(eventService.updateEvent(id, dto));
    }

    @GetMapping("/events/{id}")
    @ApiMessage("Lấy chi tiết sự kiện")
    public ResponseEntity<ResCreateEventDTO> getEventById(
            @PathVariable long id) throws IdInvalidException {
        Event event = eventService.getEventById(id);
        if (event == null) {
            throw new IdInvalidException("Không tìm thấy sự kiện với id = " + id);
        }
        return ResponseEntity.ok(eventService.convertToResCreateEventDTO(event));
    }

    @DeleteMapping("/events/{id}")
    @ApiMessage("Xoá sự kiện")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable long id) throws IdInvalidException {
        Event event = eventService.getEventById(id);
        if (event == null) {
            throw new IdInvalidException("Không tìm thấy sự kiện với id = " + id);
        }
        eventService.deleteEventById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/events")
    @ApiMessage("Lấy danh sách sự kiện")
    public ResponseEntity<ResultPaginationDTO> getAllEvents(
            @Filter Specification<Event> spec,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(spec, pageable));
    }

    @PatchMapping("/events/{id}/active")
    @ApiMessage("Bật/tắt trạng thái active")
    public ResponseEntity<Map<String, Object>> toggleActive(
            @PathVariable long id) throws Exception {

        Event event = eventService.toggleActive(id);

        return ResponseEntity.ok(Map.of(
                "id", event.getId(),
                "isActive", event.isActive(),
                "message", event.isActive() ? "Đã kích hoạt sự kiện" : "Đã tắt kích hoạt sự kiện"
        ));
    }

    @PatchMapping("/events/{id}/published")
    @ApiMessage("Bật/tắt trạng thái published")
    public ResponseEntity<Map<String, Object>> togglePublished(
            @PathVariable long id) throws Exception {

        Event event = eventService.togglePublished(id);

        return ResponseEntity.ok(Map.of(
                "id", event.getId(),
                "isPublished", event.isPublished(),
                "message", event.isPublished() ? "Đã publish sự kiện" : "Đã unpublish sự kiện"
        ));
    }
}

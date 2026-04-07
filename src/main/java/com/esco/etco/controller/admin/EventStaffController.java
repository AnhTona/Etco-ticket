package com.esco.etco.controller.admin;

import com.esco.etco.entity.request.ReqEventStaffDTO;
import com.esco.etco.entity.response.ResEventStaffDTO;
import com.esco.etco.service.EventStaffService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.constant.ApiPaths;
import com.esco.etco.util.error.IdInvalidException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.EVENT_STAFFS_API)
@RequiredArgsConstructor
public class EventStaffController {

    private final EventStaffService eventStaffService;

    @PostMapping
    @ApiMessage("Thêm nhân viên vào sự kiện")
    public ResponseEntity<ResEventStaffDTO> addStaffToEvent(@Valid @RequestBody ReqEventStaffDTO reqEventStaffDTO) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventStaffService.addStaffToEvent(reqEventStaffDTO));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa nhân viên khỏi sự kiện")
    public ResponseEntity<Void> removeStaffFromEvent(@PathVariable long id) throws IdInvalidException {
        eventStaffService.removeStaffFromEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/event/{eventId}")
    @ApiMessage("Lấy danh sách nhân viên của sự kiện")
    public ResponseEntity<List<ResEventStaffDTO>> getStaffsByEventId(@PathVariable long eventId) {
        return ResponseEntity.ok(eventStaffService.getStaffsByEventId(eventId));
    }

    @GetMapping("/user/{userId}")
    @ApiMessage("Lấy danh sách sự kiện của nhân viên")
    public ResponseEntity<List<ResEventStaffDTO>> getEventsByStaffId(@PathVariable long userId) {
        return ResponseEntity.ok(eventStaffService.getEventsByStaffId(userId));
    }
}

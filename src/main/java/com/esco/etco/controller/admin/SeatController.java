package com.esco.etco.controller.admin;

import com.esco.etco.entity.request.ReqSeatDTO;
import com.esco.etco.entity.response.ResSeatDTO;
import com.esco.etco.service.SeatService;
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
@RequestMapping(ApiPaths.SEATS_API)
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping
    @ApiMessage("Tạo mới ghế")
    public ResponseEntity<ResSeatDTO> createSeat(@Valid @RequestBody ReqSeatDTO reqSeatDTO) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatService.create(reqSeatDTO));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật ghế")
    public ResponseEntity<ResSeatDTO> updateSeat(@PathVariable long id, @Valid @RequestBody ReqSeatDTO reqSeatDTO) throws IdInvalidException {
        return ResponseEntity.ok(seatService.update(id, reqSeatDTO));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa ghế")
    public ResponseEntity<Void> deleteSeat(@PathVariable long id) throws IdInvalidException {
        seatService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy ghế theo ID")
    public ResponseEntity<ResSeatDTO> getSeatById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(seatService.getById(id));
        } catch (IdInvalidException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/event/{eventId}")
    @ApiMessage("Lấy danh sách ghế theo sự kiện")
    public ResponseEntity<List<ResSeatDTO>> getSeatsByEventId(@PathVariable long eventId) {
        return ResponseEntity.ok(seatService.getByEventId(eventId));
    }
}

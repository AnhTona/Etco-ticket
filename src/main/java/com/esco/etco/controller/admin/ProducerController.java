package com.esco.etco.controller.admin;

import com.esco.etco.entity.request.ReqProducerDTO;
import com.esco.etco.entity.response.ResProducerDTO;
import com.esco.etco.service.ProducerService;
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
@RequestMapping(ApiPaths.PRODUCERS_API)
@RequiredArgsConstructor
public class ProducerController {

    private final ProducerService producerService;

    @PostMapping
    @ApiMessage("Tạo mới nhà sản xuất")
    public ResponseEntity<ResProducerDTO> createProducer(@Valid @RequestBody ReqProducerDTO reqProducerDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(producerService.create(reqProducerDTO));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật nhà sản xuất")
    public ResponseEntity<ResProducerDTO> updateProducer(@PathVariable long id, @Valid @RequestBody ReqProducerDTO reqProducerDTO) throws IdInvalidException {
        // Service layer already handles IdInvalidException, so no redundant check needed here.
        return ResponseEntity.ok(producerService.update(id, reqProducerDTO));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Xóa nhà sản xuất")
    public ResponseEntity<Void> deleteProducer(@PathVariable long id) throws IdInvalidException {
        // Service layer already handles IdInvalidException, so no redundant check needed here.
        producerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy nhà sản xuất theo ID")
    public ResponseEntity<ResProducerDTO> getProducerById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(producerService.getById(id));
        } catch (IdInvalidException e) {
            // Return 404 Not Found if the producer does not exist
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @ApiMessage("Lấy tất cả nhà sản xuất")
    public ResponseEntity<List<ResProducerDTO>> getAllProducers() {
        return ResponseEntity.ok(producerService.getAll());
    }
}

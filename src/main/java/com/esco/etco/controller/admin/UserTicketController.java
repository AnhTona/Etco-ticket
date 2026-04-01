package com.esco.etco.controller.admin;

import com.esco.etco.entity.request.ReqUserTicketDTO;
import com.esco.etco.entity.response.ResUserTicketDTO;
import com.esco.etco.service.UserTicketService;
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
@RequestMapping(ApiPaths.USER_TICKETS_API)
@RequiredArgsConstructor
public class UserTicketController {

    private final UserTicketService userTicketService;

    @PostMapping
    @ApiMessage("Tạo mới vé người dùng")
    public ResponseEntity<ResUserTicketDTO> createUserTicket(@Valid @RequestBody ReqUserTicketDTO reqUserTicketDTO) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(userTicketService.create(reqUserTicketDTO));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật vé người dùng")
    public ResponseEntity<ResUserTicketDTO> updateUserTicket(@PathVariable long id, @Valid @RequestBody ReqUserTicketDTO reqUserTicketDTO) throws IdInvalidException {
        return ResponseEntity.ok(userTicketService.update(id, reqUserTicketDTO));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy vé người dùng theo ID")
    public ResponseEntity<ResUserTicketDTO> getUserTicketById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userTicketService.getById(id));
        } catch (IdInvalidException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @ApiMessage("Lấy tất cả vé người dùng")
    public ResponseEntity<List<ResUserTicketDTO>> getAllUserTickets() {
        return ResponseEntity.ok(userTicketService.getAll());
    }
    
    @GetMapping("/user/{userId}")
    @ApiMessage("Lấy danh sách vé theo người dùng")
    public ResponseEntity<List<ResUserTicketDTO>> getUserTicketsByUserId(@PathVariable long userId) {
        return ResponseEntity.ok(userTicketService.getByUserId(userId));
    }
}

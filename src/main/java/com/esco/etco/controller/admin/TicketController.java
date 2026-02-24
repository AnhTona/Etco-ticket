package com.esco.etco.controller.admin;

import com.esco.etco.entity.Ticket;
import com.esco.etco.entity.request.ReqTicketDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.ticket.ResCreateTicketDTO;
import com.esco.etco.entity.response.ticket.ResTicketDTO;
import com.esco.etco.entity.response.ticket.ResUpdateTicketDTO;
import com.esco.etco.service.TicketService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/tickets")
    @ApiMessage("Tạo vé mới")
    public ResponseEntity<ResCreateTicketDTO> createTicket(
            @Valid @RequestBody ReqTicketDTO dto) throws IdInvalidException {
        ResCreateTicketDTO created = this.ticketService.createTicket(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/tickets/{id}")
    @ApiMessage("Cập nhật vé")
    public ResponseEntity<ResUpdateTicketDTO> updateTicket(
            @PathVariable long id,
            @Valid @RequestBody ReqTicketDTO dto) throws IdInvalidException {
        Ticket existing = this.ticketService.getTicketById(id);
        if (existing == null) {
            throw new IdInvalidException("Ticket với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.ticketService.updateTicket(id, dto));
    }

    @GetMapping("/tickets/{id}")
    @ApiMessage("Lấy chi tiết vé")
    public ResponseEntity<ResTicketDTO> getTicketById(
            @PathVariable long id) throws IdInvalidException {
        Ticket ticket = this.ticketService.getTicketById(id);
        if (ticket == null) {
            throw new IdInvalidException("Ticket với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.ticketService.convertToResTicketDTO(ticket));
    }

    @GetMapping("/tickets")
    @ApiMessage("Lấy danh sách vé")
    public ResponseEntity<ResultPaginationDTO> getAllTickets(
            @Filter Specification<Ticket> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.ticketService.getAllTickets(spec, pageable));
    }

    @DeleteMapping("/tickets/{id}")
    @ApiMessage("Xoá vé")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable long id) throws IdInvalidException {
        Ticket ticket = this.ticketService.getTicketById(id);
        if (ticket == null) {
            throw new IdInvalidException("Ticket với id = " + id + " không tồn tại");
        }
        this.ticketService.deleteTicketById(id);
        return ResponseEntity.ok(null);
    }
}
package com.esco.etco.controller.admin;

import com.esco.etco.entity.Order;
import com.esco.etco.entity.request.ReqOrderDTO;
import com.esco.etco.entity.request.ReqPayOrderDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.order.*;
import com.esco.etco.service.OrderService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @ApiMessage("Tạo đơn hàng mới")
    public ResponseEntity<ResCreateOrderDTO> createOrder(
            @Valid @RequestBody ReqOrderDTO dto) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.orderService.createOrder(dto));
    }

    @PostMapping("/orders/pay")
    @ApiMessage("Thanh toán đơn hàng")
    public ResponseEntity<ResPayOrderDTO> payOrder(
            @Valid @RequestBody ReqPayOrderDTO dto) throws IdInvalidException {
        return ResponseEntity.ok(this.orderService.payOrder(dto));
    }

    @GetMapping("/orders/{id}")
    @ApiMessage("Xem chi tiết đơn hàng")
    public ResponseEntity<ResOrderDTO> getOrderById(
            @PathVariable long id) throws IdInvalidException {
        return ResponseEntity.ok(this.orderService.getOrderById(id));
    }

    @GetMapping("/orders")
    @ApiMessage("Danh sách đơn hàng")
    public ResponseEntity<ResultPaginationDTO> getAllOrders(
            @Filter Specification<Order> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.orderService.getAllOrders(spec, pageable));
    }

    @GetMapping("/orders/my-tickets")
    @ApiMessage("Xem vé đã mua của tôi")
    public ResponseEntity<List<ResUserTicketDTO>> getMyTickets() {
        return ResponseEntity.ok(this.orderService.getMyTickets());
    }

    @PostMapping("/orders/verify-qr")
    @ApiMessage("Xác thực QR code check-in")
    public ResponseEntity<ResUserTicketDTO> verifyQrCode(
            @RequestParam String qrCode) throws IdInvalidException {
        return ResponseEntity.ok(this.orderService.verifyQrCode(qrCode));
    }
}
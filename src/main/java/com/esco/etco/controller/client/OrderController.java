package com.esco.etco.controller.client;

import com.esco.etco.entity.Order;
import com.esco.etco.entity.request.ReqOrderDTO;
import com.esco.etco.entity.request.ReqPayOrderDTO;
import com.esco.etco.entity.response.ResSeatRecommendationDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.order.*;
import com.esco.etco.service.OrderService;
import com.esco.etco.service.SeatService;
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
    private final SeatService seatService;

    public OrderController(OrderService orderService, SeatService seatService) {
        this.orderService = orderService;
        this.seatService = seatService;
    }

    @PostMapping("/orders")
    @ApiMessage("Tạo đơn hàng mới")
    public ResponseEntity<ResCreateOrderDTO> createOrder(
            @Valid @RequestBody ReqOrderDTO dto) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.orderService.createOrder(dto));
    }
    @PostMapping("/orders/{id}/cancel")
    @ApiMessage("Hủy đơn hàng và giải phóng ghế")
    public ResponseEntity<Void> cancelOrder(@PathVariable long id) throws IdInvalidException {
        this.orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
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

    @PostMapping("/seats/recommend-adjacent")
    @ApiMessage("Kiểm tra và Gợi ý ghế trống kế bên")
    public ResponseEntity<ResSeatRecommendationDTO> recommendAdjacentSeats(
            @RequestParam long eventId,
            @RequestBody List<String> selectedSeatLabels) {

        return ResponseEntity.ok(seatService.getRecommendedSeats(eventId, selectedSeatLabels));
    }
}

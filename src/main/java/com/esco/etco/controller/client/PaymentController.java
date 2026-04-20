package com.esco.etco.controller.client;

import com.esco.etco.service.impl.PaymentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin(origins = "http://localhost:4173")
public class PaymentController {

    private final PaymentServiceImpl paymentServiceImpl;

    public PaymentController(PaymentServiceImpl paymentServiceImpl){
        this.paymentServiceImpl = paymentServiceImpl;
    }

    // Gửi yêu cầu tạo thanh toán từ React
    // Gửi yêu cầu tạo thanh toán từ React
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> requestBody) {
        try {
            // Validate dữ liệu đầu vào (Thêm kiểm tra orderId)
            if (!requestBody.containsKey("amount") || !requestBody.containsKey("orderInfo") || !requestBody.containsKey("orderId")) {
                return ResponseEntity.badRequest().body("Thiếu thông tin amount, orderInfo hoặc orderId");
            }

            String amount = requestBody.get("amount").toString();
            String orderInfo = requestBody.get("orderInfo").toString();
            String orderId = requestBody.get("orderId").toString(); // Hứng orderId từ React

            // Truyền thêm orderId vào Service
            Map<String, Object> response = this.paymentServiceImpl.createPayment(amount, orderInfo, orderId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment");
        }
    }

    // Hứng kết quả từ MoMo (IPN Webhook)
    @PostMapping("/ipn")
    public ResponseEntity<?> processIpn(@RequestBody Map<String, Object> body) {
        try {
            System.out.println("MoMo IPN nhận được: " + body);

            // Chuyển toàn bộ logic xác thực và cập nhật DB cho Service xử lý
            paymentServiceImpl.processIpn(body);

            // MoMo yêu cầu trả về HTTP 204 (No Content) hoặc 200 (OK) nếu xử lý IPN thành công
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi xác thực IPN: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

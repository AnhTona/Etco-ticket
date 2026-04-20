package com.esco.etco.service.impl;

import com.esco.etco.util.MoMoSecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.return-url}")
    private String returnUrl;

    @Value("${momo.ipn-url}")
    private String ipnUrl;


    public Map<String, Object> createPayment(String amount, String orderInfo, String dbOrderId) throws Exception {

        // MẸO Ở ĐÂY: Nối thêm timestamp để ID gửi sang MoMo luôn luôn khác nhau (Ví dụ: "11_17123456789")
        String momoOrderId = dbOrderId + "_" + System.currentTimeMillis();

        String requestId = momoOrderId;
        String extraData = "";
        String requestType = "captureWallet";

        // Tạo chuỗi signature chuẩn theo tài liệu MoMo
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + momoOrderId + // Truyền cái ID đã nối timestamp vào đây
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + returnUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = MoMoSecurityUtil.signHmacSHA256(rawSignature, secretKey);

        // Build Body gửi sang MoMo
        Map<String, Object> momoRequest = new HashMap<>();
        momoRequest.put("partnerCode", partnerCode);
        momoRequest.put("partnerName", "EvtGO Ticket");
        momoRequest.put("storeId", "EvtGOStore");
        momoRequest.put("requestId", requestId);
        momoRequest.put("amount", amount);
        momoRequest.put("orderId", momoOrderId); // Truyền ID đã nối timestamp vào đây
        momoRequest.put("orderInfo", orderInfo);
        momoRequest.put("redirectUrl", returnUrl);
        momoRequest.put("ipnUrl", ipnUrl);
        momoRequest.put("lang", "vi");
        momoRequest.put("extraData", extraData);
        momoRequest.put("requestType", requestType);
        momoRequest.put("signature", signature);

        // Gọi API MoMo
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, momoRequest, Map.class);

        return response.getBody();
    }

    public void processIpn(Map<String, Object> body) throws Exception {
        // Kiểm tra chữ ký hợp lệ
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + body.get("amount") +
                "&extraData=" + body.get("extraData") +
                "&message=" + body.get("message") +
                "&orderId=" + body.get("orderId") +
                "&orderInfo=" + body.get("orderInfo") +
                "&orderType=" + body.get("orderType") +
                "&partnerCode=" + body.get("partnerCode") +
                "&payType=" + body.get("payType") +
                "&requestId=" + body.get("requestId") +
                "&responseTime=" + body.get("responseTime") +
                "&resultCode=" + body.get("resultCode");

        String verifySignature = MoMoSecurityUtil.signHmacSHA256(rawSignature, secretKey);

        if (!verifySignature.equals(body.get("signature"))) {
            throw new IllegalArgumentException("Invalid signature from MoMo");
        }

        // Xử lý kết quả thanh toán
        int resultCode = Integer.parseInt(body.get("resultCode").toString());

        // Lấy orderId từ MoMo (Vd: "11_17123456789")
        String momoOrderId = (String) body.get("orderId");

        // CẮT BỎ TIMESTAP ĐỂ LẤY ID THẬT CỦA DATABASE
        String realOrderId = momoOrderId.split("_")[0]; // Kết quả sẽ là "11"

        if (resultCode == 0) {
            // Sử dụng realOrderId để truy vấn Database
            System.out.println("Đơn hàng ID thật là: " + realOrderId + " đã thanh toán thành công!");
            // TODO: Tìm đơn hàng bằng realOrderId và cập nhật thành PAID
        } else {
            System.out.println("Đơn hàng ID thật là: " + realOrderId + " thanh toán thất bại. Mã lỗi: " + resultCode);
            // TODO: Cập nhật thành FAILED
        }
    }
}

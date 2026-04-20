package com.esco.etco.service;

import com.esco.etco.entity.request.ReqMoMoPaymentDTO;

import java.util.Map;

public interface PaymentService {
    Map<String, Object> createMoMoPayment(ReqMoMoPaymentDTO dto) throws Exception;
    void processMoMoIpn(Map<String, Object> body) throws Exception;
}

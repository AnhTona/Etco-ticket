package com.esco.etco.service;

import com.esco.etco.entity.Order;
import com.esco.etco.entity.request.ReqOrderDTO;
import com.esco.etco.entity.request.ReqPayOrderDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.order.ResCreateOrderDTO;
import com.esco.etco.entity.response.order.ResOrderDTO;
import com.esco.etco.entity.response.order.ResPayOrderDTO;
import com.esco.etco.entity.response.order.ResUserTicketDTO;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderService {

    ResCreateOrderDTO createOrder(ReqOrderDTO dto) throws IdInvalidException;

    ResPayOrderDTO payOrder(ReqPayOrderDTO dto) throws IdInvalidException;

    ResOrderDTO getOrderById(long id) throws IdInvalidException;

    ResultPaginationDTO getAllOrders(Specification<Order> spec, Pageable pageable);

    List<ResUserTicketDTO> getMyTickets();

    ResUserTicketDTO verifyQrCode(String qrCode) throws IdInvalidException;

    void cancelOrder(long id) throws IdInvalidException;
}
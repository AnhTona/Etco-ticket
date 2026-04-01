package com.esco.etco.service.impl;

import com.esco.etco.entity.*;
import com.esco.etco.entity.request.ReqOrderDTO;
import com.esco.etco.entity.request.ReqPayOrderDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.order.*;
import com.esco.etco.repository.*;
import com.esco.etco.service.OrderService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.constant.OrderStatusEnum;
import com.esco.etco.util.constant.TicketEnum;
import com.esco.etco.util.constant.UserTicketStatusEnum;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final UserTicketRepository userTicketRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            TicketRepository ticketRepository,
                            UserRepository userRepository,
                            UserTicketRepository userTicketRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.userTicketRepository = userTicketRepository;
    }

    @Override
    @Transactional
    public ResCreateOrderDTO createOrder(ReqOrderDTO dto) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new IdInvalidException("Vui lòng đăng nhập"));
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser == null) {
            throw new IdInvalidException("User không tồn tại");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IdInvalidException("Đơn hàng phải có ít nhất 1 item");
        }

        Order order = new Order();
        order.setOrderCode(generateOrderCode());
        order.setOrderStatus(OrderStatusEnum.PENDING);
        order.setUser(currentUser);

        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (ReqOrderDTO.OrderItemDTO itemDTO : dto.getItems()) {
            Ticket ticket = this.ticketRepository.findById(itemDTO.getTicketId()).orElse(null);
            if (ticket == null) {
                throw new IdInvalidException("Ticket với id = " + itemDTO.getTicketId() + " không tồn tại");
            }

            if (ticket.getTicketStatus() != TicketEnum.PUBLISHED) {
                throw new IdInvalidException("Vé '" + ticket.getTicketType()
                        + "' của sự kiện '" + ticket.getEvent().getName()
                        + "' hiện không còn bán");
            }

            int remaining = ticket.getTotalQuantity() - ticket.getSoldQuantity();
            if (itemDTO.getQuantity() > remaining) {
                throw new IdInvalidException("Vé '" + ticket.getTicketType()
                        + "' chỉ còn " + remaining + " vé");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setTicket(ticket);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPricePerUnit(ticket.getPrice());
            orderItem.setSubtotal(ticket.getPrice() * itemDTO.getQuantity());
            orderItem.setOrder(order);

            totalAmount += orderItem.getSubtotal();
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = this.orderRepository.save(order);
        return convertToResCreateOrderDTO(savedOrder);
    }

    @Override
    @Transactional
    public ResPayOrderDTO payOrder(ReqPayOrderDTO dto) throws IdInvalidException {
        Order order = this.orderRepository.findById(dto.getOrderId()).orElse(null);
        if (order == null) {
            throw new IdInvalidException("Order với id = " + dto.getOrderId() + " không tồn tại");
        }

        if (order.getOrderStatus() != OrderStatusEnum.PENDING) {
            throw new IdInvalidException("Đơn hàng này đã được xử lý (trạng thái: " + order.getOrderStatus() + ")");
        }

        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new IdInvalidException("Vui lòng đăng nhập"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new IdInvalidException("Bạn không có quyền thanh toán đơn hàng này");
        }

        List<UserTicket> allUserTickets = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            Ticket ticket = item.getTicket();
            int remaining = ticket.getTotalQuantity() - ticket.getSoldQuantity();
            if (item.getQuantity() > remaining) {
                throw new IdInvalidException("Vé '" + ticket.getTicketType() + "' chỉ còn " + remaining + " vé.");
            }

            ticket.setSoldQuantity(ticket.getSoldQuantity() + item.getQuantity());
            if (ticket.getSoldQuantity() >= ticket.getTotalQuantity()) {
                ticket.setTicketStatus(TicketEnum.SOLD_OUT);
            }
            this.ticketRepository.save(ticket);

            for (int i = 0; i < item.getQuantity(); i++) {
                UserTicket userTicket = new UserTicket();
                userTicket.setQrCode(UUID.randomUUID().toString());
                userTicket.setStatus(UserTicketStatusEnum.VALID);
                userTicket.setIssuedAt(Instant.now());
                userTicket.setUser(order.getUser());
                userTicket.setTicket(ticket);
                userTicket.setEvent(ticket.getEvent());
                userTicket.setOrder(order);
                allUserTickets.add(userTicket);
            }
        }

        this.userTicketRepository.saveAll(allUserTickets);
        order.setOrderStatus(OrderStatusEnum.PAID);
        order.setPaidAt(Instant.now());
        this.orderRepository.save(order);

        return convertToResPayOrderDTO(order, allUserTickets);
    }

    @Override
    public ResOrderDTO getOrderById(long id) throws IdInvalidException {
        Order order = this.orderRepository.findById(id).orElse(null);
        if (order == null) throw new IdInvalidException("Order không tồn tại");

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        
        // Kiểm tra quyền sở hữu (trừ khi là admin)
        boolean isAdmin = currentUser != null && currentUser.getRole() != null && "SUPER_ADMIN".equals(currentUser.getRole().getName());
        if (!isAdmin && (currentUser == null || order.getUser().getId() != currentUser.getId())) {
             throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền xem đơn hàng này");
        }

        return convertToResOrderDTO(order);
    }

    @Override
    public ResultPaginationDTO getAllOrders(Specification<Order> spec, Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userRepository.findByEmail(email);
        
        Specification<Order> finalSpec = spec;
        boolean isAdmin = currentUser != null && currentUser.getRole() != null && "SUPER_ADMIN".equals(currentUser.getRole().getName());
        if (!isAdmin) {
             long userId = currentUser != null ? currentUser.getId() : -1L;
             Specification<Order> userSpec = (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
             finalSpec = spec == null ? userSpec : spec.and(userSpec);
        }

        Page<Order> pageOrder = this.orderRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageOrder.getTotalPages());
        mt.setTotal(pageOrder.getTotalElements());
        rs.setMeta(mt);

        rs.setResult(pageOrder.getContent().stream().map(this::convertToResOrderDTO).collect(Collectors.toList()));
        return rs;
    }

    @Override
    public List<ResUserTicketDTO> getMyTickets() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userRepository.findByEmail(email);
        if (user == null) return new ArrayList<>();

        return this.userTicketRepository.findByUserId(user.getId()).stream()
                .map(this::convertToResUserTicketDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResUserTicketDTO verifyQrCode(String qrCode) throws IdInvalidException {
        UserTicket userTicket = this.userTicketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new IdInvalidException("QR code không hợp lệ"));

        if (userTicket.getStatus() == UserTicketStatusEnum.USED)
            throw new IdInvalidException("Vé này đã được sử dụng lúc: " + userTicket.getUsedAt());

        userTicket.setStatus(UserTicketStatusEnum.USED);
        userTicket.setUsedAt(Instant.now());
        return convertToResUserTicketDTO(this.userTicketRepository.save(userTicket));
    }

    private String generateOrderCode() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ORD-" + date + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private ResUserTicketDTO convertToResUserTicketDTO(UserTicket ut) {
        ResUserTicketDTO res = new ResUserTicketDTO();
        res.setId(ut.getId());
        res.setQrCode(ut.getQrCode());
        res.setStatus(ut.getStatus().name());
        res.setIssuedAt(ut.getIssuedAt());
        res.setUsedAt(ut.getUsedAt());

        if (ut.getEvent() != null) {
            ResUserTicketDTO.EventDTO eventDTO = new ResUserTicketDTO.EventDTO();
            eventDTO.setId(ut.getEvent().getId());
            eventDTO.setName(ut.getEvent().getName());
            eventDTO.setLocation(ut.getEvent().getLocation());
            eventDTO.setStartTime(ut.getEvent().getStartTime());
            eventDTO.setEndTime(ut.getEvent().getEndTime());

            // Merge: Xử lý logic lấy ảnh cover cho vé
            if (ut.getEvent().getImages() != null && !ut.getEvent().getImages().isEmpty()) {
                String coverImage = ut.getEvent().getImages().stream()
                        .filter(EventImage::isCover)
                        .map(EventImage::getUrl)
                        .findFirst()
                        .orElse(ut.getEvent().getImages().get(0).getUrl());
                eventDTO.setImage(coverImage);
            }
            res.setEvent(eventDTO);
        }

        if (ut.getTicket() != null) {
            ResUserTicketDTO.TicketDTO ticketDTO = new ResUserTicketDTO.TicketDTO();
            ticketDTO.setId(ut.getTicket().getId());
            ticketDTO.setTicketType(ut.getTicket().getTicketType() != null ? ut.getTicket().getTicketType().name() : null);
            ticketDTO.setPrice(ut.getTicket().getPrice());
            res.setTicket(ticketDTO);
        }
        return res;
    }

    private ResCreateOrderDTO convertToResCreateOrderDTO(Order order) {
        ResCreateOrderDTO res = new ResCreateOrderDTO();
        res.setId(order.getId());
        res.setOrderCode(order.getOrderCode());
        res.setTotalAmount(order.getTotalAmount());
        res.setOrderStatus(order.getOrderStatus().name());
        res.setCreatedAt(order.getCreatedAt());
        res.setCreatedBy(order.getCreatedBy());

        res.setItems(order.getOrderItems().stream().map(item -> {
            ResCreateOrderDTO.OrderItemDTO dto = new ResCreateOrderDTO.OrderItemDTO();
            dto.setId(item.getId());
            dto.setTicketId(item.getTicket().getId());
            dto.setTicketType(item.getTicket().getTicketType() != null ? item.getTicket().getTicketType().name() : null);
            dto.setEventName(item.getTicket().getEvent() != null ? item.getTicket().getEvent().getName() : null);
            dto.setQuantity(item.getQuantity());
            dto.setPricePerUnit(item.getPricePerUnit());
            dto.setSubtotal(item.getSubtotal());
            return dto;
        }).collect(Collectors.toList()));
        return res;
    }

    private ResOrderDTO convertToResOrderDTO(Order order) {
        ResOrderDTO res = new ResOrderDTO();
        res.setId(order.getId());
        res.setOrderCode(order.getOrderCode());
        res.setTotalAmount(order.getTotalAmount());
        res.setOrderStatus(order.getOrderStatus().name());
        res.setPaidAt(order.getPaidAt());
        res.setCreatedAt(order.getCreatedAt());
        res.setCreatedBy(order.getCreatedBy());

        if (order.getUser() != null) {
            ResOrderDTO.UserDTO userDTO = new ResOrderDTO.UserDTO();
            userDTO.setId(order.getUser().getId());
            userDTO.setName(order.getUser().getName());
            userDTO.setEmail(order.getUser().getEmail());
            res.setUser(userDTO);
        }

        res.setItems(order.getOrderItems().stream().map(item -> {
            ResOrderDTO.OrderItemDTO dto = new ResOrderDTO.OrderItemDTO();
            dto.setId(item.getId());
            dto.setTicketId(item.getTicket().getId());
            dto.setTicketType(item.getTicket().getTicketType() != null ? item.getTicket().getTicketType().name() : null);
            dto.setEventName(item.getTicket().getEvent() != null ? item.getTicket().getEvent().getName() : null);
            dto.setQuantity(item.getQuantity());
            dto.setPricePerUnit(item.getPricePerUnit());
            dto.setSubtotal(item.getSubtotal());
            return dto;
        }).collect(Collectors.toList()));
        return res;
    }

    private ResPayOrderDTO convertToResPayOrderDTO(Order order, List<UserTicket> userTickets) {
        ResPayOrderDTO res = new ResPayOrderDTO();
        res.setOrderId(order.getId());
        res.setOrderCode(order.getOrderCode());
        res.setOrderStatus(order.getOrderStatus().name());
        res.setPaidAt(order.getPaidAt());
        res.setTotalAmount(order.getTotalAmount());

        res.setUserTickets(userTickets.stream().map(ut -> {
            ResPayOrderDTO.UserTicketDTO dto = new ResPayOrderDTO.UserTicketDTO();
            dto.setId(ut.getId());
            dto.setQrCode(ut.getQrCode());
            dto.setEventName(ut.getEvent() != null ? ut.getEvent().getName() : null);
            dto.setTicketType(ut.getTicket() != null && ut.getTicket().getTicketType() != null ? ut.getTicket().getTicketType().name() : null);
            dto.setStatus(ut.getStatus().name());
            dto.setIssuedAt(ut.getIssuedAt());
            return dto;
        }).collect(Collectors.toList()));
        return res;
    }
}
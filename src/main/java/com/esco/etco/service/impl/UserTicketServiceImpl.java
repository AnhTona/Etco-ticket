package com.esco.etco.service.impl;

import com.esco.etco.entity.*;
import com.esco.etco.entity.request.ReqUserTicketDTO;
import com.esco.etco.entity.response.ResUserTicketDTO;
import com.esco.etco.repository.*;
import com.esco.etco.service.UserTicketService;
import com.esco.etco.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserTicketServiceImpl implements UserTicketService {
    private final UserTicketRepository userTicketRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final SeatRepository seatRepository;

    private ResUserTicketDTO toDto(UserTicket userTicket) {
        ResUserTicketDTO dto = new ResUserTicketDTO();
        dto.setId(userTicket.getId());
        dto.setQrCode(userTicket.getQrCode());
        dto.setStatus(userTicket.getStatus());
        dto.setIssuedAt(userTicket.getIssuedAt());
        dto.setUsedAt(userTicket.getUsedAt());
        if (userTicket.getUser() != null) dto.setUserId(userTicket.getUser().getId());
        if (userTicket.getTicket() != null) dto.setTicketId(userTicket.getTicket().getId());
        if (userTicket.getEvent() != null) dto.setEventId(userTicket.getEvent().getId());
        if (userTicket.getOrder() != null) dto.setOrderId(userTicket.getOrder().getId());
        if (userTicket.getSeat() != null) dto.setSeatId(userTicket.getSeat().getId());
        return dto;
    }

    private void mapDtoToEntity(UserTicket userTicket, ReqUserTicketDTO dto) throws IdInvalidException {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IdInvalidException("User không tồn tại với id: " + dto.getUserId()));
        Ticket ticket = ticketRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new IdInvalidException("Ticket không tồn tại với id: " + dto.getTicketId()));
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new IdInvalidException("Event không tồn tại với id: " + dto.getEventId()));
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IdInvalidException("Order không tồn tại với id: " + dto.getOrderId()));
        
        Seat seat = null;
        if (dto.getSeatId() != null) {
            seat = seatRepository.findById(dto.getSeatId())
                    .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + dto.getSeatId()));
        }

        userTicket.setQrCode(dto.getQrCode());
        userTicket.setStatus(dto.getStatus());
        userTicket.setUser(user);
        userTicket.setTicket(ticket);
        userTicket.setEvent(event);
        userTicket.setOrder(order);
        userTicket.setSeat(seat);
    }

    @Override
    public ResUserTicketDTO create(ReqUserTicketDTO reqUserTicketDTO) throws IdInvalidException {
        UserTicket userTicket = new UserTicket();
        mapDtoToEntity(userTicket, reqUserTicketDTO);
        return toDto(userTicketRepository.save(userTicket));
    }

    @Override
    public ResUserTicketDTO update(long id, ReqUserTicketDTO reqUserTicketDTO) throws IdInvalidException {
        UserTicket userTicket = userTicketRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("UserTicket không tồn tại với id: " + id));
        mapDtoToEntity(userTicket, reqUserTicketDTO);
        return toDto(userTicketRepository.save(userTicket));
    }

    @Override
    public ResUserTicketDTO getById(long id) throws IdInvalidException {
        UserTicket userTicket = userTicketRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("UserTicket không tồn tại với id: " + id));
        return toDto(userTicket);
    }

    @Override
    public List<ResUserTicketDTO> getAll() {
        return userTicketRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ResUserTicketDTO> getByUserId(long userId) {
        return userTicketRepository.findByUserId(userId).stream().map(this::toDto).collect(Collectors.toList());
    }
}

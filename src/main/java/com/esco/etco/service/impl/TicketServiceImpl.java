package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.Ticket;
import com.esco.etco.entity.request.ReqTicketDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.ticket.ResCreateTicketDTO;
import com.esco.etco.entity.response.ticket.ResTicketDTO;
import com.esco.etco.entity.response.ticket.ResUpdateTicketDTO;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.repository.TicketRepository;
import com.esco.etco.service.TicketService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.constant.EventTicketEnum;
import com.esco.etco.util.constant.TicketEnum;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketServiceImpl(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public ResCreateTicketDTO createTicket(ReqTicketDTO dto) throws IdInvalidException {
        Ticket ticket = new Ticket();
        mapDtoToEntity(dto, ticket);

        ticket.setSoldQuantity(0);
        ticket.setTicketStatus(TicketEnum.PUBLISHED);

        Ticket saved = this.ticketRepository.save(ticket);
        return convertToResCreateTicketDTO(saved);
    }

    @Override
    public ResUpdateTicketDTO updateTicket(long id, ReqTicketDTO dto) throws IdInvalidException {
        Ticket ticket = this.ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            throw new IdInvalidException("Ticket với id = " + id + " không tồn tại");
        }

        mapDtoToEntity(dto, ticket);
        ticket.setUpdatedAt(Instant.now());
        ticket.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));

        Ticket saved = this.ticketRepository.save(ticket);
        return convertToResUpdateTicketDTO(saved);
    }

    @Override
    public Ticket getTicketById(long id) {
        Optional<Ticket> ticketOptional = this.ticketRepository.findById(id);
        return ticketOptional.orElse(null);
    }

    @Override
    public void deleteTicketById(long id) throws IdInvalidException {
        Ticket ticket = this.ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            throw new IdInvalidException("Ticket với id = " + id + " không tồn tại");
        }
        this.ticketRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO getAllTickets(Specification<Ticket> spec, Pageable pageable) {
        Page<Ticket> pageTicket = this.ticketRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageTicket.getTotalPages());
        mt.setTotal(pageTicket.getTotalElements());

        rs.setMeta(mt);

        List<ResTicketDTO> listDTO = pageTicket.getContent().stream()
                .map(this::convertToResTicketDTO)
                .collect(Collectors.toList());

        rs.setResult(listDTO);

        return rs;
    }

    @Override
    public ResCreateTicketDTO convertToResCreateTicketDTO(Ticket ticket) {
        ResCreateTicketDTO res = new ResCreateTicketDTO();
        res.setId(ticket.getId());
        res.setTotalQuantity(ticket.getTotalQuantity());
        res.setSoldQuantity(ticket.getSoldQuantity());
        res.setTicketType(ticket.getTicketType() != null ? ticket.getTicketType().name() : null);
        res.setTicketStatus(ticket.getTicketStatus() != null ? ticket.getTicketStatus().name() : null);
        res.setCreatedBy(ticket.getCreatedBy());
        res.setCreatedAt(ticket.getCreatedAt());

        if (ticket.getEvent() != null) {
            ResCreateTicketDTO.EventTicket eventTicket = new ResCreateTicketDTO.EventTicket();
            eventTicket.setId(ticket.getEvent().getId());
            eventTicket.setName(ticket.getEvent().getName());
            res.setEvent(eventTicket);
        }

        return res;
    }

    @Override
    public ResUpdateTicketDTO convertToResUpdateTicketDTO(Ticket ticket) {
        ResUpdateTicketDTO res = new ResUpdateTicketDTO();
        res.setId(ticket.getId());
        res.setTotalQuantity(ticket.getTotalQuantity());
        res.setSoldQuantity(ticket.getSoldQuantity());
        res.setTicketType(ticket.getTicketType() != null ? ticket.getTicketType().name() : null);
        res.setTicketStatus(ticket.getTicketStatus() != null ? ticket.getTicketStatus().name() : null);
        res.setUpdatedBy(ticket.getUpdatedBy());
        res.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getEvent() != null) {
            ResUpdateTicketDTO.EventTicket eventTicket = new ResUpdateTicketDTO.EventTicket();
            eventTicket.setId(ticket.getEvent().getId());
            eventTicket.setName(ticket.getEvent().getName());
            res.setEvent(eventTicket);
        }

        return res;
    }

    @Override
    public ResTicketDTO convertToResTicketDTO(Ticket ticket) {
        ResTicketDTO res = new ResTicketDTO();
        res.setId(ticket.getId());
        res.setTotalQuantity(ticket.getTotalQuantity());
        res.setSoldQuantity(ticket.getSoldQuantity());
        res.setTicketType(ticket.getTicketType() != null ? ticket.getTicketType().name() : null);
        res.setTicketStatus(ticket.getTicketStatus() != null ? ticket.getTicketStatus().name() : null);
        res.setCreatedBy(ticket.getCreatedBy());
        res.setCreatedAt(ticket.getCreatedAt());
        res.setUpdatedBy(ticket.getUpdatedBy());
        res.setUpdatedAt(ticket.getUpdatedAt());

        if (ticket.getEvent() != null) {
            ResTicketDTO.EventTicket eventTicket = new ResTicketDTO.EventTicket();
            eventTicket.setId(ticket.getEvent().getId());
            eventTicket.setName(ticket.getEvent().getName());
            res.setEvent(eventTicket);
        }

        return res;
    }

    private void mapDtoToEntity(ReqTicketDTO dto, Ticket ticket) throws IdInvalidException {
        ticket.setTotalQuantity(dto.getTotalQuantity());

        if (dto.getTicketType() != null) {
            ticket.setTicketType(EventTicketEnum.valueOf(dto.getTicketType()));
        }
        if (dto.getTicketStatus() != null) {
            ticket.setTicketStatus(TicketEnum.valueOf(dto.getTicketStatus()));
        }

        // Gán event
        Event event = this.eventRepository.findById(dto.getEventId()).orElse(null);
        if (event == null) {
            throw new IdInvalidException("Event với id = " + dto.getEventId() + " không tồn tại");
        }
        ticket.setEvent(event);
    }
}
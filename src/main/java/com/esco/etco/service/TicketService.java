package com.esco.etco.service;

import com.esco.etco.entity.Ticket;
import com.esco.etco.entity.request.ReqTicketDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.ticket.ResCreateTicketDTO;
import com.esco.etco.entity.response.ticket.ResTicketDTO;
import com.esco.etco.entity.response.ticket.ResUpdateTicketDTO;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface TicketService {

    ResCreateTicketDTO createTicket(ReqTicketDTO dto) throws IdInvalidException;

    ResUpdateTicketDTO updateTicket(long id, ReqTicketDTO dto) throws IdInvalidException;

    Ticket getTicketById(long id);

    void deleteTicketById(long id) throws IdInvalidException;

    ResultPaginationDTO getAllTickets(Specification<Ticket> spec, Pageable pageable);

    ResCreateTicketDTO convertToResCreateTicketDTO(Ticket ticket);

    ResUpdateTicketDTO convertToResUpdateTicketDTO(Ticket ticket);

    ResTicketDTO convertToResTicketDTO(Ticket ticket);
}
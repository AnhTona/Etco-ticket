package com.esco.etco.service;

import com.esco.etco.entity.request.ReqUserTicketDTO;
import com.esco.etco.entity.response.ResUserTicketDTO;
import com.esco.etco.util.error.IdInvalidException;

import java.util.List;

public interface UserTicketService {
    ResUserTicketDTO create(ReqUserTicketDTO reqUserTicketDTO) throws IdInvalidException;
    ResUserTicketDTO update(long id, ReqUserTicketDTO reqUserTicketDTO) throws IdInvalidException;
    ResUserTicketDTO getById(long id) throws IdInvalidException;
    List<ResUserTicketDTO> getAll();
    List<ResUserTicketDTO> getByUserId(long userId);
}

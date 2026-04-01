package com.esco.etco.service;

import com.esco.etco.entity.request.ReqSeatDTO;
import com.esco.etco.entity.response.ResSeatDTO;
import com.esco.etco.util.error.IdInvalidException;

import java.util.List;

public interface SeatService {
    ResSeatDTO create(ReqSeatDTO reqSeatDTO) throws IdInvalidException;
    ResSeatDTO update(long id, ReqSeatDTO reqSeatDTO) throws IdInvalidException;
    void delete(long id) throws IdInvalidException;
    ResSeatDTO getById(long id) throws IdInvalidException;
    List<ResSeatDTO> getByEventId(long eventId);
}

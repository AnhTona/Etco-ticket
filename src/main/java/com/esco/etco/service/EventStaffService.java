package com.esco.etco.service;

import com.esco.etco.entity.request.ReqEventStaffDTO;
import com.esco.etco.entity.response.ResEventStaffDTO;
import com.esco.etco.util.error.IdInvalidException;

import java.util.List;

public interface EventStaffService {
    ResEventStaffDTO addStaffToEvent(ReqEventStaffDTO reqEventStaffDTO) throws IdInvalidException;
    void removeStaffFromEvent(long id) throws IdInvalidException;
    List<ResEventStaffDTO> getStaffsByEventId(long eventId);
    List<ResEventStaffDTO> getEventsByStaffId(long userId);
}

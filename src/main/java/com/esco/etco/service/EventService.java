package com.esco.etco.service;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.request.ReqEventDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.event.ResCreateEventDTO;
import com.esco.etco.entity.response.event.ResUpdateEventDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public interface EventService {
    ResCreateEventDTO createEvent(ReqEventDTO dto);

    ResUpdateEventDTO updateEvent(long id, ReqEventDTO dto);

    Event getEventById(long id);

    void deleteEventById(long id);

    ResultPaginationDTO getAllEvents(Specification<Event> spec, Pageable pageable);

    ResCreateEventDTO convertToResCreateEventDTO(Event event);

    ResUpdateEventDTO convertToResUpdateEventDTO(Event event);

    Event toggleActive(long id) throws Exception;

    Event togglePublished(long id) throws Exception;

}

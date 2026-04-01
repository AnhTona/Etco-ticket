package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.Seat;
import com.esco.etco.entity.request.ReqSeatDTO;
import com.esco.etco.entity.response.ResSeatDTO;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.repository.SeatRepository;
import com.esco.etco.service.SeatService;
import com.esco.etco.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final EventRepository eventRepository;

    private ResSeatDTO toDto(Seat seat) {
        ResSeatDTO dto = new ResSeatDTO();
        dto.setId(seat.getId());
        dto.setSeatLabel(seat.getSeatLabel());
        dto.setZone(seat.getZone());
        dto.setPrice(seat.getPrice());
        dto.setStatus(seat.getStatus());
        if (seat.getEvent() != null) {
            dto.setEventId(seat.getEvent().getId());
        }
        dto.setCreatedAt(seat.getCreatedAt());
        dto.setUpdatedAt(seat.getUpdatedAt());
        dto.setCreatedBy(seat.getCreatedBy());
        dto.setUpdatedBy(seat.getUpdatedBy());
        return dto;
    }

    @Override
    public ResSeatDTO create(ReqSeatDTO reqSeatDTO) throws IdInvalidException {
        Event event = eventRepository.findById(reqSeatDTO.getEventId())
                .orElseThrow(() -> new IdInvalidException("Event không tồn tại với id: " + reqSeatDTO.getEventId()));

        Seat seat = new Seat();
        seat.setSeatLabel(reqSeatDTO.getSeatLabel());
        seat.setZone(reqSeatDTO.getZone());
        seat.setPrice(reqSeatDTO.getPrice());
        seat.setStatus(reqSeatDTO.getStatus());
        seat.setEvent(event);
        return toDto(seatRepository.save(seat));
    }

    @Override
    public ResSeatDTO update(long id, ReqSeatDTO reqSeatDTO) throws IdInvalidException {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + id));
        Event event = eventRepository.findById(reqSeatDTO.getEventId())
                .orElseThrow(() -> new IdInvalidException("Event không tồn tại với id: " + reqSeatDTO.getEventId()));

        seat.setSeatLabel(reqSeatDTO.getSeatLabel());
        seat.setZone(reqSeatDTO.getZone());
        seat.setPrice(reqSeatDTO.getPrice());
        seat.setStatus(reqSeatDTO.getStatus());
        seat.setEvent(event);
        return toDto(seatRepository.save(seat));
    }

    @Override
    public void delete(long id) throws IdInvalidException {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + id));
        seatRepository.delete(seat);
    }

    @Override
    public ResSeatDTO getById(long id) throws IdInvalidException {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Seat không tồn tại với id: " + id));
        return toDto(seat);
    }

    @Override
    public List<ResSeatDTO> getByEventId(long eventId) {
        return seatRepository.findByEventId(eventId).stream().map(this::toDto).collect(Collectors.toList());
    }
}

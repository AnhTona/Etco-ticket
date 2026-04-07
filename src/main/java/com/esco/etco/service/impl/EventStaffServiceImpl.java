package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.EventStaff;
import com.esco.etco.entity.Role;
import com.esco.etco.entity.User;
import com.esco.etco.entity.request.ReqEventStaffDTO;
import com.esco.etco.entity.response.ResEventStaffDTO;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.repository.EventStaffRepository;
import com.esco.etco.repository.RoleRepository;
import com.esco.etco.repository.UserRepository;
import com.esco.etco.service.EventStaffService;
import com.esco.etco.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventStaffServiceImpl implements EventStaffService {

    private final EventStaffRepository eventStaffRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private ResEventStaffDTO toDto(EventStaff eventStaff) {
        ResEventStaffDTO dto = new ResEventStaffDTO();
        dto.setId(eventStaff.getId());
        if (eventStaff.getEvent() != null) {
            dto.setEventId(eventStaff.getEvent().getId());
            dto.setEventName(eventStaff.getEvent().getName());
        }
        if (eventStaff.getUser() != null) {
            dto.setUserId(eventStaff.getUser().getId());
            dto.setUserName(eventStaff.getUser().getName());
            dto.setUserEmail(eventStaff.getUser().getEmail());
        }
        dto.setCreatedAt(eventStaff.getCreatedAt());
        dto.setCreatedBy(eventStaff.getCreatedBy());
        return dto;
    }

    @Override
    public ResEventStaffDTO addStaffToEvent(ReqEventStaffDTO reqEventStaffDTO) throws IdInvalidException {
        if (eventStaffRepository.existsByEventIdAndUserId(reqEventStaffDTO.getEventId(), reqEventStaffDTO.getUserId())) {
            throw new IdInvalidException("User " + reqEventStaffDTO.getUserId() + " is already staff for event " + reqEventStaffDTO.getEventId());
        }

        Event event = eventRepository.findById(reqEventStaffDTO.getEventId())
                .orElseThrow(() -> new IdInvalidException("Event không tồn tại với id: " + reqEventStaffDTO.getEventId()));
        User user = userRepository.findById(reqEventStaffDTO.getUserId())
                .orElseThrow(() -> new IdInvalidException("User không tồn tại với id: " + reqEventStaffDTO.getUserId()));

        // Tự động cấp quyền STAFF cho user nếu chưa có role STAFF hoặc cao hơn
        if (user.getRole() == null || user.getRole().getName().equals("CUSTOMER")) {
            Role staffRole = roleRepository.findByName("STAFF");
            if (staffRole != null) {
                user.setRole(staffRole);
                userRepository.save(user);
            }
        }

        EventStaff eventStaff = new EventStaff();
        eventStaff.setEvent(event);
        eventStaff.setUser(user);

        return toDto(eventStaffRepository.save(eventStaff));
    }

    @Override
    public void removeStaffFromEvent(long id) throws IdInvalidException {
        EventStaff eventStaff = eventStaffRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("EventStaff record không tồn tại với id: " + id));
        eventStaffRepository.delete(eventStaff);
    }

    @Override
    public List<ResEventStaffDTO> getStaffsByEventId(long eventId) {
        return eventStaffRepository.findByEventId(eventId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResEventStaffDTO> getEventsByStaffId(long userId) {
        return eventStaffRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.EventImage;
import com.esco.etco.entity.request.ReqEventDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.event.ResCreateEventDTO;
import com.esco.etco.entity.response.event.ResEventDTO;
import com.esco.etco.entity.response.event.ResUpdateEventDTO;
import com.esco.etco.repository.EventImageRepository;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.service.EventService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.error.IdInvalidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceimpl implements EventService {

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final EventRepository eventRepository;
    private final EventImageRepository eventImageRepository; // ★ Thêm

    public EventServiceimpl(EventRepository eventRepository,
                            EventImageRepository eventImageRepository) {
        this.eventRepository = eventRepository;
        this.eventImageRepository = eventImageRepository;
    }

    @Override
    public ResCreateEventDTO createEvent(ReqEventDTO dto) {
        Event event = new Event();
        mapDtoToEntity(dto, event);

        event.setCreatedAt(Instant.now());
        event.setCreatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));
        event.setActive(false);
        event.setPublished(false);

        Event saved = eventRepository.save(event);
        return convertToResCreateEventDTO(saved);
    }

    @Override
    public ResUpdateEventDTO updateEvent(long id, ReqEventDTO dto) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) return null;

        mapDtoToEntity(dto, event);
        event.setUpdatedAt(Instant.now());
        event.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));

        Event saved = eventRepository.save(event);
        return convertToResUpdateEventDTO(saved);
    }

    @Override
    public Event getEventById(long id) {
        Optional<Event> eventOptional = this.eventRepository.findById(id);
        return eventOptional.orElse(null);
    }

    @Override
    public void deleteEventById(long id) {
        this.eventRepository.deleteById(id);
    }

    @Override
    public Event toggleActive(long id) throws Exception {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sự kiện với id = " + id));

        // Đảo trạng thái: true → false, false → true
        event.setActive(!event.isActive());
        event.setUpdatedAt(Instant.now());
        event.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));

        return eventRepository.save(event);
    }

    @Override
    public Event togglePublished(long id) throws Exception {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sự kiện với id = " + id));

        // Không cho publish nếu event đã qua startTime
        if (!event.isPublished() && event.getStartTime() != null
                && event.getStartTime().isBefore(Instant.now())) {
            throw new IdInvalidException(
                    "Không thể publish sự kiện đã qua thời gian bắt đầu.");
        }

        // Đảo trạng thái
        event.setPublished(!event.isPublished());
        event.setUpdatedAt(Instant.now());
        event.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));

        return eventRepository.save(event);
    }

    @Override
    public ResultPaginationDTO getAllEvents(Specification<Event> spec, Pageable pageable) {
        Page<Event> pageEvent = this.eventRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageEvent.getTotalPages());
        mt.setTotal(pageEvent.getTotalElements());
        result.setMeta(mt);

        List<Event> events = pageEvent.getContent();

        // Lấy tất cả eventId trong page này
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // 1 query duy nhất lấy TẤT CẢ ảnh của các event trong page
        List<EventImage> allImages = eventImageRepository.findByEventIdIn(eventIds);

        // Group ảnh theo eventId → Map<eventId, List<EventImage>>
        Map<Long, List<EventImage>> imagesByEventId = allImages.stream()
                .collect(Collectors.groupingBy(img -> img.getEvent().getId()));

        // Convert sang DTO, truyền ảnh đã query sẵn
        List<ResEventDTO> listDTO = events.stream()
                .map(event -> convertToResEventDTO(event, imagesByEventId.getOrDefault(event.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        result.setResult(listDTO);
        return result;
    }

    private boolean computePublished(Event event) {
        if (event.getStartTime() != null && event.getStartTime().isBefore(Instant.now())) {
            return false;
        }
        return event.isPublished();
    }

    private ResEventDTO convertToResEventDTO(Event event, List<EventImage> images) {
        ResEventDTO dto = new ResEventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setActive(event.isActive());
        dto.setCreatedBy(event.getCreatedBy());
        dto.setCreatedAt(event.getCreatedAt());

        dto.setPublished(computePublished(event));

        if (event.getStartTime() != null) {
            ZonedDateTime start = event.getStartTime().atZone(ZONE_VN);
            dto.setStartDate(start.toLocalDate().toString());
            dto.setStartTime(start.toLocalTime().toString());
        }
        if (event.getEndTime() != null) {
            ZonedDateTime end = event.getEndTime().atZone(ZONE_VN);
            dto.setEndDate(end.toLocalDate().toString());
            dto.setEndTime(end.toLocalTime().toString());
        }

        // Convert ảnh từ list đã truyền vào (KHÔNG dùng event.getImages())
        dto.setImages(images.stream()
                .map(img -> {
                    ResEventDTO.ImageDTO imgDTO = new ResEventDTO.ImageDTO();
                    imgDTO.setId(img.getId());
                    imgDTO.setUrl(img.getUrl());
                    imgDTO.setCover(img.isCover());
                    return imgDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }

    @Override
    public ResCreateEventDTO convertToResCreateEventDTO(Event event) {
        ResCreateEventDTO res = new ResCreateEventDTO();
        res.setId(event.getId());
        res.setName(event.getName());
        res.setDescription(event.getDescription());
        res.setLocation(event.getLocation());
        res.setActive(event.isActive());
        res.setCreatedBy(event.getCreatedBy());
        res.setCreatedAt(event.getCreatedAt());

        res.setPublished(computePublished(event));

        // Query ảnh riêng từ EventImageRepository
        List<EventImage> images = eventImageRepository.findByEventId(event.getId());
        res.setUrlImage(images.stream()
                .map(EventImage::getUrl)
                .collect(Collectors.toList()));

        if (event.getStartTime() != null) {
            ZonedDateTime start = event.getStartTime().atZone(ZONE_VN);
            res.setStartDate(start.toLocalDate().toString());
            res.setStartTime(start.toLocalTime().toString());
        }
        if (event.getEndTime() != null) {
            ZonedDateTime end = event.getEndTime().atZone(ZONE_VN);
            res.setEndDate(end.toLocalDate().toString());
            res.setEndTime(end.toLocalTime().toString());
        }

        return res;
    }

    @Override
    public ResUpdateEventDTO convertToResUpdateEventDTO(Event event) {
        ResUpdateEventDTO res = new ResUpdateEventDTO();
        res.setId(event.getId());
        res.setName(event.getName());
        res.setDescription(event.getDescription());
        res.setLocation(event.getLocation());
        res.setActive(event.isActive());
        res.setUpdateBy(event.getUpdatedBy());
        res.setUpdateAt(event.getUpdatedAt());

        res.setPublished(computePublished(event));

        // Query ảnh riêng từ EventImageRepository
        List<EventImage> images = eventImageRepository.findByEventId(event.getId());
        res.setUrlImage(images.stream()
                .map(EventImage::getUrl)
                .collect(Collectors.toList()));

        if (event.getStartTime() != null) {
            ZonedDateTime start = event.getStartTime().atZone(ZONE_VN);
            res.setStartDate(start.toLocalDate().toString());
            res.setStartTime(start.toLocalTime().toString());
        }
        if (event.getEndTime() != null) {
            ZonedDateTime end = event.getEndTime().atZone(ZONE_VN);
            res.setEndDate(end.toLocalDate().toString());
            res.setEndTime(end.toLocalTime().toString());
        }

        return res;
    }

    private void mapDtoToEntity(ReqEventDTO dto, Event event) {
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setPermitNumber(dto.getPermitNumber());
        event.setPermitIssuedBy(dto.getPermitIssuedBy());
        event.setLocation(dto.getLocation());
        event.setPermitIssuedAt(parseDateToInstant(dto.getPermitIssuedAt()));
        event.setStartTime(combineDateAndTime(dto.getStartDate(), dto.getStartTime()));
        event.setEndTime(combineDateAndTime(dto.getEndDate(), dto.getEndTime()));
    }

    private Instant combineDateAndTime(String dateStr, String timeStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalTime time = LocalTime.parse(timeStr);
        ZonedDateTime zdt = ZonedDateTime.of(date, time, ZONE_VN);
        return zdt.toInstant();
    }

    private Instant parseDateToInstant(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        ZonedDateTime zdt = date.atStartOfDay(ZONE_VN);
        return zdt.toInstant();
    }
}
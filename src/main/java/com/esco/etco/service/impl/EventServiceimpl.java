package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.EventImage;
import com.esco.etco.entity.Producer;
import com.esco.etco.entity.request.ReqEventDTO;
import com.esco.etco.entity.response.ResultPaginationDTO;
import com.esco.etco.entity.response.event.ResCreateEventDTO;
import com.esco.etco.entity.response.event.ResEventDTO;
import com.esco.etco.entity.response.event.ResUpdateEventDTO;
import com.esco.etco.repository.EventImageRepository;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.repository.ProducerRepository;
import com.esco.etco.service.EventService;
import com.esco.etco.service.FileService;
import com.esco.etco.util.SecurityUtil;
import com.esco.etco.util.error.IdInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

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
import com.esco.etco.entity.Genre;

@Service
@Slf4j
public class EventServiceimpl implements EventService {

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    private final FileService fileService;
    private final EventRepository eventRepository;
    private final EventImageRepository eventImageRepository;
    private final ProducerRepository producerRepository;

    public EventServiceimpl(EventRepository eventRepository,
                            EventImageRepository eventImageRepository,
                            FileService fileService,
                            ProducerRepository producerRepository) {
        this.eventRepository = eventRepository;
        this.eventImageRepository = eventImageRepository;
        this.fileService = fileService;
        this.producerRepository = producerRepository;
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
    @Transactional // QUAN TRỌNG: Đảm bảo xóa sạch các bảng liên quan trong 1 transaction
    public void deleteEventById(long id) {
        // 1. Tìm sự kiện trong DB
        Event event = this.eventRepository.findById(id).orElse(null);
        if (event == null) {
            log.error("Không tìm thấy sự kiện để xóa với ID: " + id);
            return;
        }

        // 2. Xóa các tệp ảnh vật lý trong thư mục storage trước
        List<EventImage> images = this.eventImageRepository.findByEventId(id);
        String folder = "events/" + id;

        for (EventImage image : images) {
            try {
                fileService.deleteFile(image.getUrl(), folder);
            } catch (Exception e) {
                log.error("Không thể xóa file vật lý: " + image.getUrl());
            }
        }

        try {
            fileService.deleteDirectory(folder);
        } catch (Exception e) {
            log.error("Không thể xóa folder vật lý: " + folder);
        }

        // 3. Xóa Entity Event
        // Nhờ cấu hình cascade = CascadeType.ALL trong Event.java,
        // Hibernate sẽ tự động xóa các bản ghi con trong bảng tickets, event_images, event_artists
        this.eventRepository.delete(event);
        log.info("Đã xóa thành công sự kiện ID: " + id + " và các dữ liệu liên quan.");
    }

    @Override
    public Event toggleActive(long id) throws Exception {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sự kiện với id = " + id));

        event.setActive(!event.isActive());
        event.setUpdatedAt(Instant.now());
        event.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));

        return eventRepository.save(event);
    }

    @Override
    public Event togglePublished(long id) throws Exception {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy sự kiện với id = " + id));

        if (!event.isPublished() && event.getStartTime() != null
                && event.getStartTime().isBefore(Instant.now())) {
            throw new IdInvalidException("Không thể publish sự kiện đã qua thời gian bắt đầu.");
        }

        event.setPublished(!event.isPublished());
        event.setUpdatedAt(Instant.now());
        event.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));

        return eventRepository.save(event);
    }

    @Override
    public ResultPaginationDTO getAllEvents(Specification<Event> spec, Pageable pageable) {
        String currentUser = SecurityUtil.getCurrentUserLogin().orElse("");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        boolean isOrganizer = auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"));

        Specification<Event> finalSpec = spec;

        if (isOrganizer && !isAdmin) {
            Specification<Event> ownerSpec = (root, query, cb) -> cb.equal(root.get("createdBy"), currentUser);
            finalSpec = (spec == null) ? ownerSpec : spec.and(ownerSpec);
        } else if (!isAdmin && !isOrganizer) {
            Specification<Event> publicSpec = (root, query, cb) -> cb.and(
                    cb.isTrue(root.get("isActive")),
                    cb.isTrue(root.get("isPublished")),
                    cb.greaterThan(root.get("startTime"), Instant.now())
            );
            finalSpec = (spec == null) ? publicSpec : spec.and(publicSpec);
        }

        Page<Event> pageEvent = this.eventRepository.findAll(finalSpec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageEvent.getTotalPages());
        mt.setTotal(pageEvent.getTotalElements());
        result.setMeta(mt);

        List<Event> events = pageEvent.getContent();

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());

        List<EventImage> allImages = eventImageRepository.findByEventIdIn(eventIds);
        Map<Long, List<EventImage>> imagesByEventId = allImages.stream()
                .collect(Collectors.groupingBy(img -> img.getEvent().getId()));

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

        dto.setPermitNumber(event.getPermitNumber());
        dto.setPermitIssuedAt(event.getPermitIssuedAt());
        dto.setPermitIssuedBy(event.getPermitIssuedBy());
        dto.setPublished(computePublished(event));

        if (event.getGenre() != null) {
            ResEventDTO.GenreDTO genreDTO = new ResEventDTO.GenreDTO();
            genreDTO.setId(event.getGenre().getId());
            genreDTO.setName(event.getGenre().getName());
            dto.setGenre(genreDTO);
        }

        // Bổ sung Producer cho Response để Admin/Organizer xem lại được
        if (event.getProducer() != null) {
            ResEventDTO.ProducerDTO pDTO = new ResEventDTO.ProducerDTO();
            pDTO.setProducerName(event.getProducer().getProducerName());
            pDTO.setContactEmail(event.getProducer().getContactEmail());
            pDTO.setBankName(event.getProducer().getBankName());
            pDTO.setBankAccountNumber(event.getProducer().getBankAccountNumber());
            dto.setProducer(pDTO);
        }

        if (event.getArtists() != null) {
            dto.setArtists(new java.util.ArrayList<>(event.getArtists()));
        }

        formatDateTime(event, dto::setStartDate, dto::setStartTime, dto::setEndDate, dto::setEndTime);

        dto.setImages(images.stream().map(img -> {
            ResEventDTO.ImageDTO imgDTO = new ResEventDTO.ImageDTO();
            imgDTO.setId(img.getId());
            imgDTO.setUrl(img.getUrl());
            imgDTO.setCover(img.isCover());
            return imgDTO;
        }).collect(Collectors.toList()));

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

        if (event.getGenre() != null) {
            ResCreateEventDTO.GenreDTO g = new ResCreateEventDTO.GenreDTO();
            g.setId(event.getGenre().getId());
            g.setName(event.getGenre().getName());
            res.setGenre(g);
        }

        List<EventImage> images = eventImageRepository.findByEventId(event.getId());
        res.setUrlImage(images.stream().map(EventImage::getUrl).collect(Collectors.toList()));

        formatDateTime(event, res::setStartDate, res::setStartTime, res::setEndDate, res::setEndTime);
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

        List<EventImage> images = eventImageRepository.findByEventId(event.getId());
        res.setUrlImage(images.stream().map(EventImage::getUrl).collect(Collectors.toList()));

        formatDateTime(event, res::setStartDate, res::setStartTime, res::setEndDate, res::setEndTime);
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

        if (dto.getGenreId() != null) {
            Genre genre = new Genre();
            genre.setId(dto.getGenreId());
            event.setGenre(genre);
        }

        // Logic lưu Producer từ DTO
        if (dto.getProducer() != null) {
            Producer producer = event.getProducer();
            if (producer == null) {
                producer = new Producer();
            }
            producer.setProducerName(dto.getProducer().getProducerName());
            producer.setContactEmail(dto.getProducer().getContactEmail());
            producer.setBankName(dto.getProducer().getBankName());
            producer.setBankAccountNumber(dto.getProducer().getBankAccountNumber());

            if (producer.getCreatedAt() == null) {
                producer.setCreatedAt(Instant.now());
                producer.setCreatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));
            } else {
                producer.setUpdatedAt(Instant.now());
                producer.setUpdatedBy(SecurityUtil.getCurrentUserLogin().orElse("system"));
            }
            event.setProducer(producer);
        }

        if (dto.getArtists() != null) {
            event.setArtists(new java.util.HashSet<>(dto.getArtists()));
        } else {
            event.setArtists(new java.util.HashSet<>());
        }
    }

    private void formatDateTime(Event event, java.util.function.Consumer<String> setSD,
                                java.util.function.Consumer<String> setST,
                                java.util.function.Consumer<String> setED,
                                java.util.function.Consumer<String> setET) {
        if (event.getStartTime() != null) {
            ZonedDateTime start = event.getStartTime().atZone(ZONE_VN);
            setSD.accept(start.toLocalDate().toString());
            setST.accept(start.toLocalTime().toString());
        }
        if (event.getEndTime() != null) {
            ZonedDateTime end = event.getEndTime().atZone(ZONE_VN);
            setED.accept(end.toLocalDate().toString());
            setET.accept(end.toLocalTime().toString());
        }
    }

    private Instant combineDateAndTime(String dateStr, String timeStr) {
        return ZonedDateTime.of(LocalDate.parse(dateStr), LocalTime.parse(timeStr), ZONE_VN).toInstant();
    }

    private Instant parseDateToInstant(String dateStr) {
        return LocalDate.parse(dateStr).atStartOfDay(ZONE_VN).toInstant();
    }

    @Override
    public List<ResEventDTO> getRecommendedEvents(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) return Collections.emptyList();

        List<Event> events = eventRepository.findAllById(eventIds).stream()
                .filter(Event::isActive)
                .filter(Event::isPublished)
                .filter(e -> e.getEndTime() != null && e.getEndTime().isAfter(Instant.now()))
                .collect(Collectors.toList());

        if (events.isEmpty()) return Collections.emptyList();

        List<Long> validEventIds = events.stream().map(Event::getId).collect(Collectors.toList());

        List<EventImage> allImages = eventImageRepository.findByEventIdIn(validEventIds);
        Map<Long, List<EventImage>> imagesByEventId = allImages.stream()
                .collect(Collectors.groupingBy(img -> img.getEvent().getId()));

        return events.stream()
                .map(event -> convertToResEventDTO(event, imagesByEventId.getOrDefault(event.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ResEventDTO> getFallbackRecommendations() {
        List<Event> events = eventRepository.findTop10ByIsActiveTrueAndIsPublishedTrueAndEndTimeAfterOrderByCreatedAtDesc(Instant.now());

        if (events.isEmpty()) return Collections.emptyList();

        List<Long> eventIds = events.stream().map(Event::getId).collect(Collectors.toList());

        List<EventImage> allImages = eventImageRepository.findByEventIdIn(eventIds);
        Map<Long, List<EventImage>> imagesByEventId = allImages.stream()
                .collect(Collectors.groupingBy(img -> img.getEvent().getId()));

        return events.stream()
                .map(event -> convertToResEventDTO(event, imagesByEventId.getOrDefault(event.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }
}
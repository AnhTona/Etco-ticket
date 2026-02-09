package com.esco.etco.config;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.EventImage;
import com.esco.etco.repository.EventImageRepository;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.service.FileService;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EventImageCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventImageCleanupScheduler.class);

    // Số ngày sau khi event kết thúc sẽ xóa file
    private static final int CLEANUP_AFTER_DAYS = 30;

    private final EventRepository eventRepository;
    private final EventImageRepository eventImageRepository;
    private final FileService fileService;

    public EventImageCleanupScheduler(EventRepository eventRepository,
                                      EventImageRepository eventImageRepository,
                                      FileService fileService) {
        this.eventRepository = eventRepository;
        this.eventImageRepository = eventImageRepository;
        this.fileService = fileService;
    }

    @Scheduled(cron = "0 0 2 * * *") // Mỗi ngày lúc 02:00
    @Transactional
    public void cleanupExpiredEventImages() {
        log.info("[CLEANUP] Bắt đầu dọn dẹp ảnh event hết hạn ");

        // Tính mốc thời gian: 30 ngày trước
        Instant cutoff = Instant.now().minus(CLEANUP_AFTER_DAYS, ChronoUnit.DAYS);

        // Tìm tất cả event đã kết thúc trước mốc cutoff
        List<Event> expiredEvents = eventRepository.findEventsEndedBefore(cutoff);

        if (expiredEvents.isEmpty()) {
            log.info("[CLEANUP] Không có event nào cần dọn dẹp.");
            return;
        }

        log.info("[CLEANUP] Tìm thấy {} event cần dọn dẹp ảnh.", expiredEvents.size());

        for (Event event : expiredEvents) {
            try {
                long eventId = event.getId();
                String folder = "events/" + eventId;

                // Lấy danh sách ảnh của event
                List<EventImage> images = eventImageRepository.findByEventId(eventId);

                if (images.isEmpty()) {
                    log.info("[CLEANUP] Event id={} không có ảnh, bỏ qua.", eventId);
                    continue;
                }

                // Xóa từng file ảnh trên server
                for (EventImage image : images) {
                    try {
                        fileService.deleteFile(image.getUrl(), folder);
                        log.info("[CLEANUP] Đã xóa file: {}/{}", folder, image.getUrl());
                    } catch (Exception e) {
                        log.warn("[CLEANUP] Không thể xóa file {}/{}: {}",
                                folder, image.getUrl(), e.getMessage());
                    }
                }

                // Xóa cả folder events/{eventId}
                try {
                    fileService.deleteDirectory(folder);
                    log.info("[CLEANUP] Đã xóa folder: {}", folder);
                } catch (Exception e) {
                    log.warn("[CLEANUP] Không thể xóa folder {}: {}", folder, e.getMessage());
                }

                // Xóa tất cả record ảnh trong DB
                eventImageRepository.deleteAllByEventId(eventId);
                log.info("[CLEANUP] Đã xóa {} record ảnh của event id={}", images.size(), eventId);

            } catch (Exception e) {
                log.error("[CLEANUP] Lỗi khi dọn dẹp event id={}: {}",
                        event.getId(), e.getMessage());
            }
        }

        log.info("[CLEANUP] Hoàn tất dọn dẹp");
    }
}
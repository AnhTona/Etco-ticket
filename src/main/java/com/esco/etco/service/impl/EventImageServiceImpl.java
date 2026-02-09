package com.esco.etco.service.impl;

import com.esco.etco.entity.Event;
import com.esco.etco.entity.EventImage;
import com.esco.etco.entity.response.image.ResUploadImageDTO;
import com.esco.etco.repository.EventImageRepository;
import com.esco.etco.repository.EventRepository;
import com.esco.etco.service.EventImageService;
import com.esco.etco.service.FileService;
import com.esco.etco.util.error.StorageException;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EventImageServiceImpl implements EventImageService {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    private final FileService fileService;
    private final EventRepository eventRepository;
    private final EventImageRepository eventImageRepository;

    public EventImageServiceImpl(FileService fileService,
                                 EventRepository eventRepository,
                                 EventImageRepository eventImageRepository) {
        this.fileService = fileService;
        this.eventRepository = eventRepository;
        this.eventImageRepository = eventImageRepository;
    }

    @Override
    public void validateFiles(MultipartFile[] files)throws StorageException {
        if (files == null || files.length == 0) {
            throw new StorageException("Vui lòng chọn ít nhất 1 file ảnh.");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new StorageException("Một trong các file rỗng. Vui lòng kiểm tra lại.");
            }
            String originalName = file.getOriginalFilename();
            if (originalName == null) {
                throw new StorageException("Không xác định được tên file.");
            }
            boolean isValid = ALLOWED_EXTENSIONS.stream()
                    .anyMatch(ext -> originalName.toLowerCase().endsWith("." + ext));
            if (!isValid) {
                throw new StorageException(
                        "File '" + originalName + "' không hợp lệ. Chỉ cho phép: " + ALLOWED_EXTENSIONS);
            }
        }
    }

    @Override
    public void validateCoverIndex(Integer coverIndex, int totalFiles) throws StorageException{
        if (coverIndex != null && (coverIndex < 0 || coverIndex >= totalFiles)) {
            throw new StorageException(
                    "coverIndex không hợp lệ. Phải nằm trong khoảng [0.." + (totalFiles - 1) + "]");
        }
    }

    @Override
    @Transactional
    public List<ResUploadImageDTO> uploadEventImages(long eventId, MultipartFile[] files, Integer coverIndex)
            throws Exception {

        // Validate
        this.validateFiles(files);
        this.validateCoverIndex(coverIndex, files.length);

        // Check event tồn tại
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new StorageException("Không tìm thấy sự kiện với id = " + eventId));

        // Tạo folder: events/{eventId}
        String folder = "events/" + eventId;
        this.fileService.createDirectory(this.fileService.getBaseURI() + folder);

        // Reset cover cũ (đảm bảo chỉ có 1 ảnh cover)
        eventImageRepository.clearCoverByEventId(eventId);

        // Lưu từng file
        List<ResUploadImageDTO> result = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            String storedName = this.fileService.store(files[i], folder);

            boolean isCover;
            if (coverIndex != null) {
                isCover = (i == coverIndex);
            } else {
                // Mặc định ảnh đầu tiên là cover
                isCover = (i == 0);
            }

            EventImage image = new EventImage();
            image.setUrl(storedName);
            image.setCover(isCover);
            image.setCreatedAt(Instant.now());
            image.setEvent(event);

            EventImage saved = eventImageRepository.save(image);

            result.add(new ResUploadImageDTO(
                    saved.getId(),
                    saved.getUrl(),
                    saved.isCover(),
                    saved.getCreatedAt()
            ));
        }

        return result;
    }

    @Override
    @Transactional
    public void deleteEventImage(long eventId, long imageId) throws Exception {
        // Kiểm tra event có tồn tại không
        eventRepository.findById(eventId)
                .orElseThrow(() -> new StorageException("Không tìm thấy sự kiện với id = " + eventId));

        // Tìm ảnh theo id + eventId (đảm bảo ảnh thuộc đúng event)
        EventImage image = eventImageRepository.findByIdAndEventId(imageId, eventId)
                .orElseThrow(() -> new StorageException(
                        "Không tìm thấy ảnh với id = " + imageId + " trong sự kiện id = " + eventId));

        // Xóa file vật lý trên server
        String folder = "events/" + eventId;
        this.fileService.deleteFile(image.getUrl(), folder);

        // Xóa record trong database
        eventImageRepository.delete(image);

        // Nếu ảnh vừa xóa là cover → set ảnh đầu tiên còn lại làm cover
        if (image.isCover()) {
            List<EventImage> remaining = eventImageRepository.findByEventId(eventId);
            if (!remaining.isEmpty()) {
                EventImage newCover = remaining.get(0);
                newCover.setCover(true);
                eventImageRepository.save(newCover);
            }
        }
    }

    @Override
    @Transactional
    public ResUploadImageDTO updateEventImage(long eventId, long imageId, MultipartFile newFile) throws Exception {
        // Validate file mới
        this.validateFiles(new MultipartFile[]{newFile});

        // Kiểm tra event tồn tại
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new StorageException("Không tìm thấy sự kiện với id = " + eventId));

        // Tìm ảnh cũ
        EventImage oldImage = eventImageRepository.findByIdAndEventId(imageId, eventId)
                .orElseThrow(() -> new StorageException(
                        "Không tìm thấy ảnh với id = " + imageId + " trong sự kiện id = " + eventId));

        String folder = "events/" + eventId;

        // Xóa file ảnh cũ trên server
        this.fileService.deleteFile(oldImage.getUrl(), folder);

        // Upload file ảnh mới
        String newStoredName = this.fileService.store(newFile, folder);

        // Cập nhật record trong DB (giữ nguyên id, isCover, event)
        oldImage.setUrl(newStoredName);
        oldImage.setCreatedAt(Instant.now()); // cập nhật thời gian

        EventImage saved = eventImageRepository.save(oldImage);

        return new ResUploadImageDTO(
                saved.getId(),
                saved.getUrl(),
                saved.isCover(),
                saved.getCreatedAt()
        );
    }
}
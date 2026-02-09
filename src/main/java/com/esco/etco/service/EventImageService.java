package com.esco.etco.service;

import com.esco.etco.entity.response.image.ResUploadImageDTO;
import com.esco.etco.util.error.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventImageService {

    void validateFiles(MultipartFile[] files) throws StorageException;

    void validateCoverIndex(Integer coverIndex, int totalFiles)throws StorageException;

    List<ResUploadImageDTO> uploadEventImages(long eventId, MultipartFile[] files, Integer coverIndex) throws Exception;

    void deleteEventImage(long eventId, long imageId) throws Exception;

    ResUploadImageDTO updateEventImage(long eventId, long imageId, MultipartFile newFile) throws Exception;
}
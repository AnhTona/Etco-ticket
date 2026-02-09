package com.esco.etco.service;

import com.esco.etco.util.error.IdInvalidException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@Service
public interface FileService {
    void createDirectory(String folder) throws URISyntaxException;

    String store(MultipartFile file, String folder) throws URISyntaxException, IOException;

    long getFileLength(String fileName, String folder) throws URISyntaxException;

    InputStreamResource getResource(String fileName, String folder) throws URISyntaxException, FileNotFoundException;

    String getBaseURI();

    void deleteFile(String fileName, String folder) throws URISyntaxException, IOException;

    void deleteDirectory(String folder) throws URISyntaxException, IOException;
}

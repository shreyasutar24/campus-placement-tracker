package com.placement.tracker.service;

import com.placement.tracker.entity.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    // Saves the file to disk and creates a Document record; returns the stored file name
    String storeFile(MultipartFile file, Long studentId, Document.DocumentType documentType, Long applicationId);

    Resource loadFileAsResource(String fileName);

    List<Document> getDocumentsByStudent(Long studentId);
}
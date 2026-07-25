package com.placement.tracker.service.impl;

import com.placement.tracker.config.FileStorageConfig;
import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Document;
import com.placement.tracker.entity.Student;
import com.placement.tracker.exception.BadRequestException;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.ApplicationRepository;
import com.placement.tracker.repository.DocumentRepository;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.service.FileService;
import com.placement.tracker.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileStorageConfig fileStorageConfig; // ← Path ki jagah FileStorageConfig

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private Path getStoragePath() {
        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory.", e);
        }
        return path;
    }

    @Override
    public String storeFile(MultipartFile file, Long studentId,
                             Document.DocumentType documentType, Long applicationId) {

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty.");
        }

        String originalName = file.getOriginalFilename();
        if (!FileStorageUtil.isAllowedExtension(originalName)) {
            throw new BadRequestException("File type not allowed. Use PDF, DOC, DOCX, JPG, PNG.");
        }

        String fileName = FileStorageUtil.generateUniqueFileName(originalName);

        try {
            Path targetLocation = getStoragePath().resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file: " + fileName, e);
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        Application application = null;
        if (applicationId != null) {
            application = applicationRepository.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));
        }

        Document document = new Document();
        document.setStudent(student);
        document.setApplication(application);
        document.setFileName(originalName);
        document.setFilePath(fileName);
        document.setDocumentType(documentType);
        documentRepository.save(document);

        // Update Student.resumeFileName so the profile page reflects the upload
        if (documentType == Document.DocumentType.RESUME) {
            student.setResumeFileName(fileName);  // store the unique name, not originalName
            studentRepository.save(student);
        }

        return fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = getStoragePath().resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("File not found: " + fileName);
        }
    }

    @Override
    public List<Document> getDocumentsByStudent(Long studentId) {
        return documentRepository.findByStudent_Id(studentId);
    }
}
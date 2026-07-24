package com.placement.tracker.controller;

import com.placement.tracker.entity.Document;
import com.placement.tracker.entity.Student;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.repository.UserRepository;
import com.placement.tracker.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Upload resume (no applicationId needed for resume)
    @PostMapping("/upload/resume")
    public ResponseEntity<String> uploadResume(@RequestParam("file") MultipartFile file,
                                                Authentication auth) {
        Student student = getStudent(auth);
        String fileName = fileService.storeFile(file, student.getId(), Document.DocumentType.RESUME, null);
        return ResponseEntity.ok("Resume uploaded: " + fileName);
    }

    // Upload proof (applicationId required — which company's proof)
    @PostMapping("/upload/proof/{applicationId}")
    public ResponseEntity<String> uploadProof(@RequestParam("file") MultipartFile file,
                                               @PathVariable Long applicationId,
                                               Authentication auth) {
        Student student = getStudent(auth);
        String fileName = fileService.storeFile(
                file, student.getId(), Document.DocumentType.APPLICATION_PROOF, applicationId);
        return ResponseEntity.ok("Proof uploaded: " + fileName);
    }

    // View all documents of logged-in student
    @GetMapping("/my-documents")
    public ResponseEntity<List<Document>> getMyDocuments(Authentication auth) {
        Student student = getStudent(auth);
        return ResponseEntity.ok(fileService.getDocumentsByStudent(student.getId()));
    }

    // Download/view a file by name
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = fileService.loadFileAsResource(fileName);

        String contentType;
        try {
            contentType = Files.probeContentType(resource.getFile().toPath());
        } catch (Exception e) {
            contentType = null;
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private Student getStudent(Authentication auth) {
        String email = auth.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
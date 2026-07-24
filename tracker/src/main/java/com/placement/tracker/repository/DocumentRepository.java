package com.placement.tracker.repository;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Document;
import com.placement.tracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByStudent(Student student);

    List<Document> findByStudent_Id(Long studentId);

    List<Document> findByApplication(Application application);

    List<Document> findByStudent_IdAndDocumentType(Long studentId, Document.DocumentType documentType);
}
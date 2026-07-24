package com.placement.tracker.repository;

import com.placement.tracker.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {

    // Used during registration to verify the student is pre-approved
    Optional<StudentRecord> findByRollNumberAndUniversityNumber(String rollNumber, String universityNumber);

    Optional<StudentRecord> findByRollNumber(String rollNumber);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByUniversityNumber(String universityNumber);
}
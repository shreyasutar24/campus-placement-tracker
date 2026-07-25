package com.placement.tracker.service;

import com.placement.tracker.entity.StudentRecord;

import java.util.List;

public interface StudentRecordService {

    StudentRecord addStudentRecord(String rollNumber, String universityNumber, String fullName,
                                    Long departmentId, Integer passingYear, String email);

    // For bulk CSV upload by admin
    void bulkAddStudentRecords(List<StudentRecord> records);

    // Used by AuthService at registration time to block outside students
    boolean isValidStudent(String rollNumber, String universityNumber);

    List<StudentRecord> getAllStudentRecords();
}
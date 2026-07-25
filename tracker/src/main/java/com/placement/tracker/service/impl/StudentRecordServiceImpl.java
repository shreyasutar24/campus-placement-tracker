package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Department;
import com.placement.tracker.entity.StudentRecord;
import com.placement.tracker.exception.BadRequestException;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.DepartmentRepository;
import com.placement.tracker.repository.StudentRecordRepository;
import com.placement.tracker.service.StudentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentRecordServiceImpl implements StudentRecordService {

    @Autowired
    private StudentRecordRepository studentRecordRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public StudentRecord addStudentRecord(String rollNumber, String universityNumber,
                                           String fullName, Long departmentId,
                                           Integer passingYear, String email) {

        if (studentRecordRepository.existsByRollNumber(rollNumber)) {
            throw new BadRequestException("Roll number already exists: " + rollNumber);
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId));

        StudentRecord record = new StudentRecord();
        record.setRollNumber(rollNumber);
        record.setUniversityNumber(universityNumber);
        record.setFullName(fullName);
        record.setDepartment(department);
        record.setPassingYear(passingYear);
        record.setEmail(email);
        record.setRegistered(false);

        return studentRecordRepository.save(record);
    }

    @Override
    public void bulkAddStudentRecords(List<StudentRecord> records) {
        studentRecordRepository.saveAll(records);
    }

    @Override
    public boolean isValidStudent(String rollNumber, String universityNumber) {
        return studentRecordRepository
                .findByRollNumberAndUniversityNumber(rollNumber, universityNumber)
                .isPresent();
    }

    @Override
    public List<StudentRecord> getAllStudentRecords() {
        return studentRecordRepository.findAll();
    }
}
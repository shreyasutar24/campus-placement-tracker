package com.placement.tracker.service;

import com.placement.tracker.entity.Student;

import java.util.List;

public interface StudentService {

    Student getStudentByUserId(Long userId);

    Student getStudentById(Long studentId);
    void deleteStudent(Long studentId);

    Student updateProfile(Long studentId, Double cgpa, Double tenthPercentage,
                           Double twelfthPercentage, boolean hasActiveBacklog, String skills);

    List<Student> getStudentsByDepartment(Long departmentId);

    List<Student> getAllStudents();
}
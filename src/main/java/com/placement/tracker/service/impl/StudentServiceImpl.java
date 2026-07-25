package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Student;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.repository.UserRepository;
import com.placement.tracker.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;
import com.placement.tracker.exception.BadRequestException;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for userId: " + userId));
    }

    @Override
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
    }

    @Override
    public Student updateProfile(Long studentId, Double cgpa, Double tenthPercentage,
                                  Double twelfthPercentage, boolean hasActiveBacklog, String skills) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        if (cgpa != null) student.setCgpa(cgpa);
        if (tenthPercentage != null) student.setTenthPercentage(tenthPercentage);
        if (twelfthPercentage != null) student.setTwelfthPercentage(twelfthPercentage);
        student.setHasActiveBacklog(hasActiveBacklog);
        if (skills != null) student.setSkills(skills);

        return studentRepository.save(student);
    }
    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        try {
            studentRepository.delete(student);
            studentRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Cannot delete this student — they have existing applications. Remove their applications first.");
        }
    }
    @Override
    public List<Student> getStudentsByDepartment(Long departmentId) {
        return studentRepository.findByDepartment_Id(departmentId);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}
package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Student;
import com.placement.tracker.entity.Teacher;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.ApplicationRepository;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.repository.TeacherRepository;
import com.placement.tracker.repository.UserRepository;
import com.placement.tracker.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public Teacher getTeacherByUserId(Long userId) {
        return teacherRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found for userId: " + userId));
    }

    @Override
    public List<Student> getDepartmentStudents(Long teacherUserId) {
        Teacher teacher = teacherRepository.findByUser_Id(teacherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        return studentRepository.findByDepartment_Id(teacher.getDepartment().getId());
    }

    @Override
    public List<Application> getDepartmentApplications(Long teacherUserId) {
        Teacher teacher = teacherRepository.findByUser_Id(teacherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        List<Student> students = studentRepository.findByDepartment_Id(teacher.getDepartment().getId());
        return students.stream()
                .flatMap(s -> applicationRepository.findByStudent(s).stream())
                .collect(Collectors.toList());
    }
}
package com.placement.tracker.service;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Student;
import com.placement.tracker.entity.Teacher;

import java.util.List;

public interface TeacherService {

    Teacher getTeacherByUserId(Long userId);

    // Only students from this teacher's own department
    List<Student> getDepartmentStudents(Long teacherUserId);

    // Only applications from students in this teacher's department
    List<Application> getDepartmentApplications(Long teacherUserId);
}
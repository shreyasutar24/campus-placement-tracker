package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import com.placement.tracker.entity.Teacher;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.*;
import com.placement.tracker.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> getStudentDashboard(Long userId) {
        Student student = studentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Opportunity> eligible = opportunityRepository
                .findByAllowedDepartments_IdAndLastDateGreaterThanEqual(
                        student.getDepartment().getId(), LocalDate.now());

        List<Application> myApps = applicationRepository.findByStudent(student);
        long selected = myApps.stream()
                .filter(a -> a.getStatus().name().equals("SELECTED")).count();
        long interview = myApps.stream()
                .filter(a -> a.getStatus().name().equals("INTERVIEW_SCHEDULED")).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("studentName", student.getUser().getFullName());
        stats.put("department", student.getDepartment().getName());
        stats.put("eligibleOpportunities", eligible.size());
        stats.put("applied", myApps.size());
        stats.put("interviewScheduled", interview);
        stats.put("selected", selected);
        return stats;
    }

    @Override
    public Map<String, Object> getTeacherDashboard(Long userId) {
        Teacher teacher = teacherRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        Long deptId = teacher.getDepartment().getId();
        List<Student> students = studentRepository.findByDepartment_Id(deptId);

        long applied = students.stream()
                .flatMap(s -> applicationRepository.findByStudent(s).stream()).count();
        long selected = students.stream()
                .flatMap(s -> applicationRepository.findByStudent(s).stream())
                .filter(a -> a.getStatus().name().equals("SELECTED")).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("department", teacher.getDepartment().getName());
        stats.put("totalStudents", students.size());
        stats.put("totalApplications", applied);
        stats.put("selected", selected);
        return stats;
    }

    @Override
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("pendingVerification",
                applicationRepository.findByStatusAndVerifiedByAdminFalse(
                        com.placement.tracker.entity.ApplicationStatus.SELECTED).size());
        return stats;
    }
}
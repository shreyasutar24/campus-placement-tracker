package com.placement.tracker.controller;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import com.placement.tracker.entity.Teacher;
import com.placement.tracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    // Student dashboard stats
    @GetMapping("/student")
    public ResponseEntity<Map<String, Object>> studentDashboard(Authentication auth) {
        String email = auth.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Opportunity> eligible = opportunityRepository
                .findByAllowedDepartments_IdAndLastDateGreaterThanEqual(
                        student.getDepartment().getId(), LocalDate.now());
        List<Application> myApps = applicationRepository.findByStudent(student);

        long selected = myApps.stream()
                .filter(a -> a.getStatus().name().equals("SELECTED")).count();
        long interview = myApps.stream()
                .filter(a -> a.getStatus().name().equals("INTERVIEW_SCHEDULED")).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("eligibleOpportunities", eligible.size());
        stats.put("applied", myApps.size());
        stats.put("interviewScheduled", interview);
        stats.put("selected", selected);
        stats.put("studentName", user.getFullName());
        stats.put("department", student.getDepartment().getName());
        return ResponseEntity.ok(stats);
    }

    // Teacher dashboard stats
    @GetMapping("/teacher")
    public ResponseEntity<Map<String, Object>> teacherDashboard(Authentication auth) {
        String email = auth.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Teacher teacher = teacherRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

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
        return ResponseEntity.ok(stats);
    }

    // Admin dashboard stats
    @GetMapping("/admin")
    public ResponseEntity<Map<String, Object>> adminDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        return ResponseEntity.ok(stats);
    }
}
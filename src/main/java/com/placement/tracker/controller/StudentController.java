package com.placement.tracker.controller;

import com.placement.tracker.dto.StudentProfileRequest;
import com.placement.tracker.dto.StatusUpdateRequest;
import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import com.placement.tracker.repository.ApplicationRepository;
import com.placement.tracker.repository.OpportunityRepository;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.repository.UserRepository;
import com.placement.tracker.service.ApplicationService;
import com.placement.tracker.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication auth) {
        Student student = getStudent(auth);
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
        return ResponseEntity.ok(stats);
    }

    // ---- Profile ----
    @GetMapping("/profile")
    public ResponseEntity<Student> getProfile(Authentication auth) {
        return ResponseEntity.ok(getStudent(auth));
    }

    @PutMapping("/profile")
    public ResponseEntity<Student> updateProfile(Authentication auth,
                                                  @RequestBody StudentProfileRequest request) {
        Student student = getStudent(auth);
        Student updated = studentService.updateProfile(
                student.getId(),
                request.getCgpa(),
                request.getTenthPercentage(),
                request.getTwelfthPercentage(),
                request.isHasActiveBacklog(),
                request.getSkills());
        return ResponseEntity.ok(updated);
    }

    // ---- Opportunities ----
    @GetMapping("/opportunities")
    public ResponseEntity<List<Opportunity>> getOpportunities(Authentication auth) {
        Student student = getStudent(auth);
        List<Opportunity> list = opportunityRepository
                .findByAllowedDepartments_IdAndLastDateGreaterThanEqual(
                        student.getDepartment().getId(), LocalDate.now());
        return ResponseEntity.ok(list);
    }

    // ---- Applications ----
    @PostMapping("/apply/{opportunityId}")
    public ResponseEntity<Application> apply(Authentication auth,
                                              @PathVariable Long opportunityId) {
        Student student = getStudent(auth);
        return ResponseEntity.ok(applicationService.applyToOpportunity(student.getId(), opportunityId));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getMyApplications(Authentication auth) {
        Student student = getStudent(auth);
        return ResponseEntity.ok(applicationService.getApplicationsByStudent(student.getId()));
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<Application> updateStatus(@PathVariable Long applicationId,
                                                     @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
                applicationService.updateStatus(applicationId, request.getStatus(), request.getNotes()));
    }

    // ---- Helper ----
    private Student getStudent(Authentication auth) {
        String email = auth.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
}
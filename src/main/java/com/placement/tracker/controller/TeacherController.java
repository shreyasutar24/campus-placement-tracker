package com.placement.tracker.controller;

import com.placement.tracker.dto.StudentResponse;
import com.placement.tracker.dto.TeacherProfileRequest;
import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import com.placement.tracker.entity.Teacher;
import com.placement.tracker.repository.ApplicationRepository;
import com.placement.tracker.repository.OpportunityRepository;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.repository.TeacherRepository;
import com.placement.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.placement.tracker.entity.User;
import com.placement.tracker.dto.TeacherProfileResponse;
import com.placement.tracker.dto.OpportunityRequest;
import com.placement.tracker.service.OpportunityService;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
	@Autowired
	private OpportunityService opportunityService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private OpportunityRepository opportunityRepository;

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication auth) {
        Teacher teacher = getTeacher(auth);
        Long deptId = teacher.getDepartment().getId();

        List<Student> students = studentRepository.findByDepartment_Id(deptId);

        long applied = students.stream()
                .flatMap(s -> applicationRepository.findByStudent(s).stream())
                .count();

        long selected = students.stream()
                .flatMap(s -> applicationRepository.findByStudent(s).stream())
                .filter(a -> a.getStatus().name().equals("SELECTED"))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("department", teacher.getDepartment().getName());
        stats.put("totalStudents", students.size());
        stats.put("totalApplications", applied);
        stats.put("selected", selected);
        List<Opportunity> opportunities = opportunityRepository
                .findByAllowedDepartments_Id(teacher.getDepartment().getId());

        long totalCompanies = opportunities.stream()
                .map(Opportunity::getCompanyName)
                .distinct()
                .count();

        stats.put("totalCompanies", totalCompanies);

        return ResponseEntity.ok(stats);
    }

    // ---- Department Students ----
    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getDepartmentStudents(Authentication auth) {
        Teacher teacher = getTeacher(auth);

        List<Student> students = studentRepository.findByDepartment_Id(teacher.getDepartment().getId());

        List<StudentResponse> response = students.stream()
                .map(s -> new StudentResponse(
                        s.getId(),
                        s.getUser() != null ? s.getUser().getFullName() : null,
                        s.getUser() != null ? s.getUser().getEmail() : null,
                        s.getStudentRecord() != null ? s.getStudentRecord().getRollNumber() : null,
                        s.getCgpa(),
                        s.isHasActiveBacklog(),
                        s.getSkills(),
                        s.getResumeFileName()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/profile")
    public ResponseEntity<TeacherProfileResponse> getProfile(Authentication auth) {

        Teacher teacher = getTeacher(auth);

        TeacherProfileResponse response = new TeacherProfileResponse(
                teacher.getUser().getFullName(),
                teacher.getUser().getEmail(),
                teacher.getUser().getPhone(),
                teacher.getDepartment().getName(),
                "Teacher"
        );

        return ResponseEntity.ok(response);
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody TeacherProfileRequest request,
            Authentication auth) {

        Teacher teacher = getTeacher(auth);

        User user = teacher.getUser();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully.");
    }

    // ---- Department Applications ----
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getDepartmentApplications(Authentication auth) {
        Teacher teacher = getTeacher(auth);
        List<Student> students = studentRepository.findByDepartment_Id(teacher.getDepartment().getId());

        List<Application> apps = students.stream()
                .flatMap(s -> applicationRepository.findByStudent(s).stream())
                .toList();

        return ResponseEntity.ok(apps);
    }

    // ---- Opportunities (read-only, powers "Job Management" and "Companies" pages) ----
    @GetMapping("/opportunities")
    public ResponseEntity<List<Opportunity>> getOpportunities(Authentication auth) {
        Teacher teacher = getTeacher(auth);
        List<Opportunity> list = opportunityRepository
                .findByAllowedDepartments_Id(teacher.getDepartment().getId());
        return ResponseEntity.ok(list);
    }
 // ---- Opportunities: create ----
    @PostMapping("/opportunities")
    public ResponseEntity<Opportunity> createOpportunity(@RequestBody OpportunityRequest request,
                                                          Authentication auth) {
        Teacher teacher = getTeacher(auth);
        User user = getUser(auth);

        Opportunity created = opportunityService.createOpportunity(
                request.getCompanyName(), request.getJobRole(), request.getPackageLpa(),
                request.getLocation(), request.getApplyLink(), request.getLastDate(),
                request.getMinCgpa(), request.getMin10thPercentage(), request.getMin12thPercentage(),
                request.isNoActiveBacklogAllowed(), request.getRequiredSkills(),
                request.getDescription(),
                Collections.singletonList(teacher.getDepartment().getId()),
                user.getId());

        return ResponseEntity.ok(created);
    }

    // ---- Opportunities: update (only if this teacher created it) ----
    @PutMapping("/opportunities/{id}")
    public ResponseEntity<?> updateOpportunity(@PathVariable Long id,
                                                @RequestBody OpportunityRequest request,
                                                Authentication auth) {
        Teacher teacher = getTeacher(auth);
        Opportunity existing = opportunityService.getOpportunityById(id);

        

        Opportunity updated = opportunityService.updateOpportunity(
                id, request.getCompanyName(), request.getJobRole(), request.getPackageLpa(),
                request.getLocation(), request.getApplyLink(), request.getLastDate(),
                request.getMinCgpa(), request.getMin10thPercentage(), request.getMin12thPercentage(),
                request.isNoActiveBacklogAllowed(), request.getRequiredSkills(),
                request.getDescription(),
                Collections.singletonList(teacher.getDepartment().getId()));

        return ResponseEntity.ok(updated);
    }

    
    @GetMapping("/students/{studentId}/applications")
    public ResponseEntity<List<Application>> getStudentApplications(
            @PathVariable Long studentId,
            Authentication auth) {

        Teacher teacher = getTeacher(auth);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Security: teacher can view only students from their department
        if (!student.getDepartment().getId().equals(teacher.getDepartment().getId())) {
            return ResponseEntity.status(403).build();
        }

        List<Application> applications = applicationRepository.findByStudent(student);

        return ResponseEntity.ok(applications);
    }

    // ---- Helper ----
    private Teacher getTeacher(Authentication auth) {
        String email = auth.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return teacherRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
    }
    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isOwnedByTeacher(Opportunity opportunity, Authentication auth) {
        User currentUser = getUser(auth);
        return opportunity.getCreatedBy() != null
                && opportunity.getCreatedBy().getId().equals(currentUser.getId());
    }
}
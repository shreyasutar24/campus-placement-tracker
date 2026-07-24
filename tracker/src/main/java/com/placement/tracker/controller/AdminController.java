package com.placement.tracker.controller;

import com.placement.tracker.dto.DepartmentRequest;
import com.placement.tracker.dto.OpportunityRequest;
import com.placement.tracker.dto.StudentRecordRequest;
import com.placement.tracker.dto.TeacherProfileRequest;
import com.placement.tracker.dto.VerifyRequest;
import com.placement.tracker.entity.*;
import com.placement.tracker.repository.*;
import com.placement.tracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.placement.tracker.dto.StudentProfileRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private OpportunityService opportunityService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StudentRecordService studentRecordService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    
    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalOpportunities", opportunityRepository.count());
        stats.put("totalCompanies", opportunityRepository.countDistinctCompanies());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("pendingVerification", applicationService.getPendingVerification().size());
        stats.put("placedStudents", applicationRepository.countDistinctPlacedStudents());
        return ResponseEntity.ok(stats);
    }

    // ---- Departments ----
    @PostMapping("/departments")
    public ResponseEntity<Department> createDepartment(@RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(departmentService.createDepartment(request.getName(), request.getCode()));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok("Department deleted.");
    }

    // ---- Student Records (pre-approved list) ----
    @PostMapping("/student-records")
    public ResponseEntity<StudentRecord> addStudentRecord(@RequestBody StudentRecordRequest request) {
        return ResponseEntity.ok(studentRecordService.addStudentRecord(
                request.getRollNumber(), request.getUniversityNumber(),
                request.getFullName(), request.getDepartmentId(),
                request.getPassingYear(), request.getEmail()));
    }

    @GetMapping("/student-records")
    public ResponseEntity<List<StudentRecord>> getAllStudentRecords() {
        return ResponseEntity.ok(studentRecordService.getAllStudentRecords());
    }

    // ---- Students ----
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id,
                                                  @RequestBody StudentProfileRequest request) {
        Student updated = studentService.updateProfile(
                id,
                request.getCgpa(),
                request.getTenthPercentage(),
                request.getTwelfthPercentage(),
                request.isHasActiveBacklog(),
                request.getSkills());
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/students/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Student deleted.");
    }

    // ---- Opportunities ----
    @PostMapping("/opportunities")
    public ResponseEntity<Opportunity> createOpportunity(@RequestBody OpportunityRequest request,Authentication auth) {
        String email = auth.getName();
        User createdBy = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(opportunityService.createOpportunity(
                request.getCompanyName(), request.getJobRole(), request.getPackageLpa(),
                request.getLocation(), request.getApplyLink(), request.getLastDate(),
                request.getMinCgpa(), request.getMin10thPercentage(), request.getMin12thPercentage(),
                request.isNoActiveBacklogAllowed(), request.getRequiredSkills(),
                request.getDescription(), request.getAllowedDepartmentIds(), createdBy.getId()));
    }

    @GetMapping("/opportunities")
    public ResponseEntity<List<Opportunity>> getAllOpportunities() {
        return ResponseEntity.ok(opportunityService.getAllOpportunities());
    }
    


    // ---- Verification ----
    @GetMapping("/verify")
    public ResponseEntity<List<Application>> getPendingVerification() {
        return ResponseEntity.ok(applicationService.getPendingVerification());
    }
 // ---- All Applications (for admin students table / reports) ----
    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationRepository.findAll());
    }
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long id) {
        applicationRepository.deleteById(id);
        return ResponseEntity.ok("Application deleted successfully.");
    }
 // ---- Teachers ----
    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacher(
            @PathVariable Long id,
            @RequestBody TeacherProfileRequest request) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        User user = teacher.getUser();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        teacher.setDepartment(department);

        userRepository.save(user);
        teacherRepository.save(teacher);

        return ResponseEntity.ok(teacher);
    }
    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(teacherRepository.findAll());
    }

    @PutMapping("/verify/{applicationId}")
    public ResponseEntity<Application> verifyApplication(@PathVariable Long applicationId,
                                                          @RequestBody VerifyRequest request) {
        return ResponseEntity.ok(applicationService.verifyApplication(applicationId, request.isApprove()));
    }
}
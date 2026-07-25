package com.placement.tracker.controller;

import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import com.placement.tracker.repository.OpportunityRepository;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    // All active opportunities for logged-in student's department
    @GetMapping
    public ResponseEntity<List<Opportunity>> getOpportunities(Authentication auth) {
        String email = auth.getName();
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Opportunity> list = opportunityRepository
                .findByAllowedDepartments_IdAndLastDateGreaterThanEqual(
                        student.getDepartment().getId(), LocalDate.now());
        return ResponseEntity.ok(list);
    }

    // Opportunity detail by ID
    @GetMapping("/{id}")
    public ResponseEntity<Opportunity> getById(@PathVariable Long id) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opportunity not found"));
        return ResponseEntity.ok(opp);
    }
}
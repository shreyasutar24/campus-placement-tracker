package com.placement.tracker.controller;

import com.placement.tracker.dto.StatusUpdateRequest;
import com.placement.tracker.entity.Application;
import com.placement.tracker.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<List<Application>> getByOpportunity(@PathVariable Long opportunityId) {
        return ResponseEntity.ok(applicationService.getApplicationsByOpportunity(opportunityId));
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Application> updateStatus(@PathVariable Long applicationId,
                                                     @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
                applicationService.updateStatus(applicationId, request.getStatus(), request.getNotes()));
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<List<Application>> getPendingVerification() {
        return ResponseEntity.ok(applicationService.getPendingVerification());
    }

    @PutMapping("/{applicationId}/verify")
    public ResponseEntity<Application> verify(@PathVariable Long applicationId,
                                               @RequestParam boolean approve) {
        return ResponseEntity.ok(applicationService.verifyApplication(applicationId, approve));
    }
    @DeleteMapping("/{applicationId}")
    public ResponseEntity<String> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return ResponseEntity.ok("Application deleted.");
    }
}
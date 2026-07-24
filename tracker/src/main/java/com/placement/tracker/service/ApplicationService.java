package com.placement.tracker.service;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.ApplicationStatus;

import java.util.List;

public interface ApplicationService {

    // Student marks "I Applied" after using the external apply link
    Application applyToOpportunity(Long studentId, Long opportunityId);

    // Student self-updates their own progress (Applied -> Interview -> Selected etc.)
    Application updateStatus(Long applicationId, ApplicationStatus newStatus, String notes);

    List<Application> getApplicationsByStudent(Long studentId);

    List<Application> getApplicationsByOpportunity(Long opportunityId);

    // Admin verification queue - SELECTED claims not yet approved
    List<Application> getPendingVerification();

    Application verifyApplication(Long applicationId, boolean approve);
    void deleteApplication(Long id);
}
package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Application;
import com.placement.tracker.entity.ApplicationStatus;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;
import com.placement.tracker.exception.BadRequestException;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.ApplicationRepository;
import com.placement.tracker.repository.OpportunityRepository;
import com.placement.tracker.repository.StudentRepository;
import com.placement.tracker.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Override
    public Application applyToOpportunity(Long studentId, Long opportunityId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", opportunityId));

        if (applicationRepository.existsByStudentAndOpportunity(student, opportunity)) {
            throw new BadRequestException("Already applied to this opportunity.");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setOpportunity(opportunity);
        application.setStatus(ApplicationStatus.APPLIED);

        return applicationRepository.save(application);
    }

    @Override
    public Application updateStatus(Long applicationId, ApplicationStatus newStatus, String notes) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        application.setStatus(newStatus);
        application.setLastUpdated(LocalDateTime.now());
        if (notes != null) application.setNotes(notes);

        if (newStatus != ApplicationStatus.SELECTED) {
            application.setVerifiedByAdmin(false);
        }

        return applicationRepository.save(application);
    }    @Override
    public List<Application> getApplicationsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        return applicationRepository.findByStudent(student);
    }

    @Override
    public List<Application> getApplicationsByOpportunity(Long opportunityId) {
        return applicationRepository.findByOpportunity_Id(opportunityId);
    }

    @Override
    public List<Application> getPendingVerification() {
        return applicationRepository.findByStatusAndVerifiedByAdminFalse(ApplicationStatus.SELECTED);
    }

    @Override
    public Application verifyApplication(Long applicationId, boolean approve) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (approve) {
            application.setVerifiedByAdmin(true);
        } else {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setVerifiedByAdmin(false);
        }
        return applicationRepository.save(application);
    }
    @Override
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application", id);
        }
        applicationRepository.deleteById(id);
    }
}
package com.placement.tracker.service;

import com.placement.tracker.entity.Opportunity;

import java.time.LocalDate;
import java.util.List;

public interface OpportunityService {

    Opportunity createOpportunity(String companyName, String jobRole, Double packageLpa, String location,
                                   String applyLink, LocalDate lastDate, Double minCgpa,
                                   Double min10thPercentage, Double min12thPercentage,
                                   boolean noActiveBacklogAllowed, String requiredSkills,
                                   String description, List<Long> allowedDepartmentIds, Long createdByUserId);

    Opportunity getOpportunityById(Long id);

    Opportunity updateOpportunity(Long id, String companyName, String jobRole, Double packageLpa,
                                   String location, String applyLink, LocalDate lastDate,
                                   Double minCgpa, Double min10thPercentage, Double min12thPercentage,
                                   boolean noActiveBacklogAllowed, String requiredSkills,
                                   String description, List<Long> allowedDepartmentIds);

    List<Opportunity> getAllOpportunities();

    // Only opportunities matching this department, not yet past last date
    List<Opportunity> getActiveOpportunitiesForDepartment(Long departmentId);

    void deleteOpportunity(Long id);
}
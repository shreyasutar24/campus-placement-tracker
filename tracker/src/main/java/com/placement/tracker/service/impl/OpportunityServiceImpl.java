package com.placement.tracker.service.impl;

import com.placement.tracker.entity.Department;
import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.User;
import com.placement.tracker.exception.ResourceNotFoundException;
import com.placement.tracker.repository.DepartmentRepository;
import com.placement.tracker.repository.OpportunityRepository;
import com.placement.tracker.repository.UserRepository;
import com.placement.tracker.service.OpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OpportunityServiceImpl implements OpportunityService {

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Opportunity createOpportunity(String companyName, String jobRole, Double packageLpa,
                                          String location, String applyLink, LocalDate lastDate,
                                          Double minCgpa, Double min10thPercentage,
                                          Double min12thPercentage, boolean noActiveBacklogAllowed,
                                          String requiredSkills, String description,
                                          List<Long> allowedDepartmentIds, Long createdByUserId) {

        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", createdByUserId));

        Set<Department> departments = new HashSet<>();
        if (allowedDepartmentIds != null) {
            for (Long deptId : allowedDepartmentIds) {
                Department dept = departmentRepository.findById(deptId)
                        .orElseThrow(() -> new ResourceNotFoundException("Department", deptId));
                departments.add(dept);
            }
        }

        Opportunity opportunity = new Opportunity();
        opportunity.setCompanyName(companyName);
        opportunity.setJobRole(jobRole);
        opportunity.setPackageLpa(packageLpa);
        opportunity.setLocation(location);
        opportunity.setApplyLink(applyLink);
        opportunity.setLastDate(lastDate);
        opportunity.setMinCgpa(minCgpa);
        opportunity.setMin10thPercentage(min10thPercentage);
        opportunity.setMin12thPercentage(min12thPercentage);
        opportunity.setNoActiveBacklogAllowed(noActiveBacklogAllowed);
        opportunity.setRequiredSkills(requiredSkills);
        opportunity.setDescription(description);
        opportunity.setAllowedDepartments(departments);
        opportunity.setCreatedBy(createdBy);

        return opportunityRepository.save(opportunity);
    }

    @Override
    public Opportunity getOpportunityById(Long id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", id));
    }

    @Override
    public Opportunity updateOpportunity(Long id, String companyName, String jobRole, Double packageLpa,
                                          String location, String applyLink, LocalDate lastDate,
                                          Double minCgpa, Double min10thPercentage, Double min12thPercentage,
                                          boolean noActiveBacklogAllowed, String requiredSkills,
                                          String description, List<Long> allowedDepartmentIds) {

        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", id));

        Set<Department> departments = new HashSet<>();
        if (allowedDepartmentIds != null) {
            for (Long deptId : allowedDepartmentIds) {
                Department dept = departmentRepository.findById(deptId)
                        .orElseThrow(() -> new ResourceNotFoundException("Department", deptId));
                departments.add(dept);
            }
        }

        opportunity.setCompanyName(companyName);
        opportunity.setJobRole(jobRole);
        opportunity.setPackageLpa(packageLpa);
        opportunity.setLocation(location);
        opportunity.setApplyLink(applyLink);
        opportunity.setLastDate(lastDate);
        opportunity.setMinCgpa(minCgpa);
        opportunity.setMin10thPercentage(min10thPercentage);
        opportunity.setMin12thPercentage(min12thPercentage);
        opportunity.setNoActiveBacklogAllowed(noActiveBacklogAllowed);
        opportunity.setRequiredSkills(requiredSkills);
        opportunity.setDescription(description);
        opportunity.setAllowedDepartments(departments);

        return opportunityRepository.save(opportunity);
    }

    @Override
    public List<Opportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    @Override
    public List<Opportunity> getActiveOpportunitiesForDepartment(Long departmentId) {
        return opportunityRepository
                .findByAllowedDepartments_IdAndLastDateGreaterThanEqual(departmentId, LocalDate.now());
    }

    @Override
    public void deleteOpportunity(Long id) {
        if (!opportunityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Opportunity", id);
        }
        opportunityRepository.deleteById(id);
    }
}
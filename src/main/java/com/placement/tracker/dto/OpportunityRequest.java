package com.placement.tracker.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OpportunityRequest {
    private String companyName;
    private String jobRole;
    private Double packageLpa;
    private String location;
    private String applyLink;
    private LocalDate lastDate;
    private Double minCgpa;
    private Double min10thPercentage;
    private Double min12thPercentage;
    private boolean noActiveBacklogAllowed;
    private String requiredSkills;
    private String description;
    private List<Long> allowedDepartmentIds;
}
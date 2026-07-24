package com.placement.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityResponse {
    private Long id;
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
    private List<String> allowedDepartmentCodes;
}
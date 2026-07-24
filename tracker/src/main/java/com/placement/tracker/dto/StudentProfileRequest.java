package com.placement.tracker.dto;

import lombok.Data;

@Data
public class StudentProfileRequest {
    private Double cgpa;
    private Double tenthPercentage;
    private Double twelfthPercentage;
    private boolean hasActiveBacklog;
    private String skills; // comma separated
}
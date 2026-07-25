package com.placement.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String fullName;
    private String email;
    private String rollNumber;
    private Double cgpa;
    private boolean hasActiveBacklog;
    private String skills;
    private String resumeFileName;
}
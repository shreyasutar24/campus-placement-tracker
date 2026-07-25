package com.placement.tracker.dto;

import lombok.Data;

@Data
public class StudentRecordRequest {
    private String rollNumber;
    private String universityNumber;
    private String fullName;
    private Long departmentId;
    private Integer passingYear;
    private String email;
}
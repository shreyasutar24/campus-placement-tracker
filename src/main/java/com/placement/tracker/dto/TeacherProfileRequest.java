package com.placement.tracker.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TeacherProfileRequest {

    private String fullName;

    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;
    private Long departmentId;

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
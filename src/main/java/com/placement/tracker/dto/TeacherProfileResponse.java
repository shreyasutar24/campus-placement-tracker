package com.placement.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherProfileResponse {

    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String role;

}
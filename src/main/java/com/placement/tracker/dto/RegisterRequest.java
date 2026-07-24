package com.placement.tracker.dto;

import lombok.Data;

/**
 * One single register request used for all 3 roles, since you only
 * created one RegisterRequest.java file. The "role" field tells the
 * controller which type of account to create.
 *
 * Send role as one of: "STUDENT", "TEACHER", "ADMIN"
 * - STUDENT needs: rollNumber + universityNumber (verified against StudentRecord)
 * - TEACHER needs: departmentId
 * - ADMIN needs: only fullName/email/password
 */
@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String role;

    // Student only
    private String rollNumber;
    private String universityNumber; // PRN

    // Teacher only
    private Long departmentId;
 // Admin
    private String adminCode;
}
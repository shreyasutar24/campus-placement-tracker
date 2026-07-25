package com.placement.tracker.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private String adminCode; // only required/checked if the account is ROLE_ADMIN
}
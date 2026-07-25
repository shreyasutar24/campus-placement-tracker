package com.placement.tracker.service;

import com.placement.tracker.dto.AuthResponse;
import com.placement.tracker.dto.LoginRequest;
import com.placement.tracker.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
}
package com.placement.tracker.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getStudentDashboard(Long userId);
    Map<String, Object> getTeacherDashboard(Long userId);
    Map<String, Object> getAdminDashboard();
}
package com.placement.tracker.dto;

import com.placement.tracker.entity.ApplicationStatus;
import lombok.Data;

/**
 * Sent by STUDENT when they self-update their application progress
 * (e.g. Applied -> Interview Scheduled -> Selected/Rejected).
 */
@Data
public class StatusUpdateRequest {
    private ApplicationStatus status;
    private String notes;
}
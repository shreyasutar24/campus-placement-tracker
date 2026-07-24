package com.placement.tracker.dto;

import com.placement.tracker.entity.ApplicationStatus;
import lombok.Data;

/**
 * Used by ADMIN to filter the applications list by a particular status
 * (e.g. fetch all applications that are currently "SELECTED" but
 * not yet verified). Separate from StatusUpdateRequest, which is the
 * student-facing update payload.
 */
@Data
public class ApplicationStatusRequest {
    private ApplicationStatus status;
}
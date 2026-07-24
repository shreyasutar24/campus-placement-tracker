package com.placement.tracker.util;

import com.placement.tracker.entity.Opportunity;
import com.placement.tracker.entity.Student;


public class EligibilityUtil {

    private EligibilityUtil() {
    }

    public static boolean isEligible(Student student, Opportunity opportunity) {

        if (opportunity.getMinCgpa() != null && student.getCgpa() != null
                && student.getCgpa() < opportunity.getMinCgpa()) {
            return false;
        }

        if (opportunity.getMin10thPercentage() != null && student.getTenthPercentage() != null
                && student.getTenthPercentage() < opportunity.getMin10thPercentage()) {
            return false;
        }

        if (opportunity.getMin12thPercentage() != null && student.getTwelfthPercentage() != null
                && student.getTwelfthPercentage() < opportunity.getMin12thPercentage()) {
            return false;
        }

        if (opportunity.isNoActiveBacklogAllowed() && student.isHasActiveBacklog()) {
            return false;
        }

        if (!opportunity.getAllowedDepartments().isEmpty()
                && !opportunity.getAllowedDepartments().contains(student.getDepartment())) {
            return false;
        }

        return true;
    }
}
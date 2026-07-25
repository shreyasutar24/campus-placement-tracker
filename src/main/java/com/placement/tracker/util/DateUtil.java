package com.placement.tracker.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private DateUtil() {
    }

    // True if opportunity's last date hasn't passed yet
    public static boolean isStillOpen(LocalDate lastDate) {
        return !lastDate.isBefore(LocalDate.now());
    }

    public static long daysRemaining(LocalDate lastDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), lastDate);
    }
}
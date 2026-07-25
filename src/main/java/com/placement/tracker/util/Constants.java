package com.placement.tracker.util;

public class Constants {

    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    public static final String ROLE_STUDENT = "ROLE_STUDENT";
    public static final String ROLE_TEACHER = "ROLE_TEACHER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB

    private Constants() {
        // utility class - no instances needed
    }
}
package com.example.sgeproject.util;

public final class Constants {

    // Error Messages
    public static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found with id: ";
    public static final String INVALID_INPUT_MESSAGE = "Invalid input provided.";
    public static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed. Invalid username or password.";
    public static final String UNAUTHORIZED_ACCESS_MESSAGE = "You are not authorized to perform this action.";

    // Success Messages
    public static final String CREATION_SUCCESS_MESSAGE = "Resource created successfully.";
    public static final String UPDATE_SUCCESS_MESSAGE = "Resource updated successfully.";
    public static final String DELETION_SUCCESS_MESSAGE = "Resource deleted successfully.";

    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PROFESSOR = "PROFESSOR";
    public static final String ROLE_ETUDIANT = "ETUDIANT";

    // Date/Time Formats (Example)
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // Other application-specific constants
    public static final int MAX_RECLAMATION_DAYS = 7; // e.g., students have 7 days to submit a reclamation after note publication

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
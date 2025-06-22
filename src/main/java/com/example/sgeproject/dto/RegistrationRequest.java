package com.example.sgeproject.dto;

import lombok.Data; // For auto-generating getters, setters, etc.

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data // Lombok annotation for boilerplate code
public class RegistrationRequest {

    @NotBlank(message = "First name is required")
    private String prenom;

    @NotBlank(message = "Last name is required")
    private String nom;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // You can add more fields here if your registration form requires them,
    // e.g., student-specific details like formation ID or student ID number.
    // private Long formationId;
    // private String numeroMatricule;
}
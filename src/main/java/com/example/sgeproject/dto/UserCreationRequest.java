package com.example.sgeproject.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// REMOVED: import com.example.sgeproject.validation.NewUserValidation; // No longer needed

@Data
public class UserCreationRequest {

    private Long id;

    @NotBlank(message = "First name is required")
    private String prenom;

    @NotBlank(message = "Last name is required")
    private String nom;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    // Password is required for new user creation, but optional for updates if not changing
    // REMOVED: groups = NewUserValidation.class as @Validated is gone
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required") // REMOVED: groups = NewUserValidation.class
    private String confirmPassword;

    @NotBlank(message = "Role is required")
    private String role;
}
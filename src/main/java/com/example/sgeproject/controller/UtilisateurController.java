package com.example.sgeproject.controller;

import com.example.sgeproject.dto.RegistrationRequest;
import com.example.sgeproject.dto.UserCreationRequest;
import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Personne;
import com.example.sgeproject.service.PersonneService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class UtilisateurController {

    private final PersonneService personneService;

    public UtilisateurController(PersonneService personneService) {
        this.personneService = personneService;
    }

    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/manualLogin")
    public String processManualLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        Optional<Personne> userOptional = personneService.authenticateUser(email, password);

        if (userOptional.isPresent()) {
            Personne user = userOptional.get();
            redirectAttributes.addFlashAttribute("successMessage", "Login successful! Welcome, " + user.getPrenom() + "!");

            session.setAttribute("loggedInUserId", user.getId());
            session.setAttribute("loggedInUserRole", user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else if ("PROFESSOR".equals(user.getRole())) {
                return "redirect:/professor/dashboard";
            } else if ("ETUDIANT".equals(user.getRole())) {
                return "redirect:/etudiant/dashboard";
            } else {
                return "redirect:/home";
            }
        } else {
            model.addAttribute("errorMessage", "Invalid email or password.");
            return "login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationRequest") RegistrationRequest request,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) { return "register"; }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match.");
            return "register";
        }
        try {
            personneService.registerEtudiant(request);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.duplicate", e.getMessage());
            return "register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred during registration.");
            System.err.println("Registration error: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/dashboard")
    public String generalDashboard(Model model) {
        return "home";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboardPage(Model model) {
        return "dashboardAdmin";
    }

    @GetMapping("/professor/dashboard")
    public String professorDashboardPage(Model model) {
        return "dashboardProfessor";
    }

    @GetMapping("/etudiant/dashboard")
    public String etudiantDashboardPage(Model model) {
        return "dashboardEtudiant";
    }

    @GetMapping("/admin/gestionUtilisateurs")
    public String manageUsers(Model model) {
        List<Personne> users = personneService.getAllPersonnes();
        model.addAttribute("users", users);
        return "gestionUtilisateurs";
    }

    @GetMapping("/admin/users/new")
    public String showAdminCreateUserForm(Model model) {
        model.addAttribute("userCreationRequest", new UserCreationRequest());
        model.addAttribute("roles", Arrays.asList("ETUDIANT", "PROFESSOR", "ADMIN"));
        return "createUserForm";
    }

    @PostMapping("/admin/users")
    public String adminCreateUser(@Valid @ModelAttribute("userCreationRequest") UserCreationRequest request,
                                  BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("roles", Arrays.asList("ETUDIANT", "PROFESSOR", "ADMIN"));
        if (result.hasErrors()) { return "createUserForm"; }
        if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "password.mismatch", "Passwords do not match.");
            return "createUserForm";
        }
        try {
            personneService.createUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            return "redirect:/admin/gestionUtilisateurs";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.duplicate", e.getMessage());
            return "createUserForm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while creating user: " + e.getMessage());
            System.err.println("Admin create user error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/admin/users/new";
        }
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Personne user = personneService.getPersonneById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        UserCreationRequest request = new UserCreationRequest();
        request.setId(user.getId()); request.setNom(user.getNom()); request.setPrenom(user.getPrenom());
        request.setEmail(user.getEmail()); request.setRole(user.getRole());
        model.addAttribute("userCreationRequest", request);
        model.addAttribute("roles", Arrays.asList("ETUDIANT", "PROFESSOR", "ADMIN"));
        return "editUserForm";
    }

    @PostMapping("/admin/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("userCreationRequest") UserCreationRequest request,
                             BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("roles", Arrays.asList("ETUDIANT", "PROFESSOR", "ADMIN"));

        // --- CRITICAL FIX: Remove the problematic 'else' block for password clearing ---
        // This block was causing the "No message found under code ''" error.
        // Validation for password fields will now only occur if the fields are non-empty.
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "password.mismatch", "New passwords do not match.");
                return "editUserForm";
            }
            // If passwords are provided and match, check their size/blankness
            // This is implicitly done by @Valid on the DTO.
            // No 'result.hasErrors()' check needed here if the specific rejectValue for mismatch is handled.
        }
        // Removed: else {
        // Removed:     result.rejectValue("password", null, null);
        // Removed:     result.rejectValue("confirmPassword", null, null);
        // Removed: }

        // Check for *all* errors, including those on password fields if they were entered,
        // or other fields.
        if (result.hasErrors()) { return "editUserForm"; }

        try {
            request.setId(id);
            personneService.updateUser(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/admin/gestionUtilisateurs";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "email.duplicate", e.getMessage());
            return "editUserForm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
            System.err.println("Admin update user error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            personneService.deletePersonne(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/gestionUtilisateurs";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("infoMessage", "You have been logged out.");
        return "redirect:/login";
    }
}
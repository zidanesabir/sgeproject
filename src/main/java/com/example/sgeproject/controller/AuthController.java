//package com.example.sgeproject.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//public class AuthController {
//
//    // Simple GET mapping for the login page
//    @GetMapping("/login")
//    public String showLoginPage() {
//        return "login"; // Renders src/main/resources/templates/login.html
//    }
//
//    // Basic simulated login (no actual authentication or user roles without Spring Security)
//    @PostMapping("/login")
//    public String processLogin(
//            @RequestParam("username") String username,
//            @RequestParam("password") String password,
//            RedirectAttributes redirectAttributes,
//            Model model) {
//
//        // --- SIMULATED LOGIN LOGIC (REPLACE WITH REAL AUTHENTICATION IF NEEDED) ---
//        // For a real application, you would inject an AuthService and validate credentials against a database.
//        if ("admin".equals(username) && "admin".equals(password)) {
//            // Simulate successful login
//            redirectAttributes.addFlashAttribute("successMessage", "Connexion réussie !");
//            // Redirect to the home page or a dashboard
//            return "redirect:/home"; // Redirects to /home endpoint
//        } else {
//            // Simulate failed login
//            model.addAttribute("errorMessage", "Nom d'utilisateur ou mot de passe incorrect.");
//            return "login"; // Return to login page with error
//        }
//    }
//
//    // Basic simulated logout
//    @GetMapping("/logout")
//    public String logout(RedirectAttributes redirectAttributes) {
//        // In a real app, this would invalidate the session
//        redirectAttributes.addFlashAttribute("infoMessage", "Vous avez été déconnecté.");
//        return "redirect:/login"; // Redirects to the login page
//    }
//
//    // Home page (accessible after simulated login)
//    @GetMapping("/home")
//    public String home(Model model) {
//        model.addAttribute("pageTitle", "Accueil du Système");
//        model.addAttribute("welcomeMessage", "Bienvenue sur le Système de Gestion des Examens !");
//        return "home"; // Renders src/main/resources/templates/home.html
//    }
//}

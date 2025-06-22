package com.example.sgeproject.controller;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EtudiantDashboardController {

    @GetMapping("/etudiant/dashboard")
    public String showDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // récupère le nom de l'utilisateur connecté
        model.addAttribute("username", username);
        return "dashboardEtudiant";  // correspond au fichier Thymeleaf etudiant-dashboard.html
    }
}

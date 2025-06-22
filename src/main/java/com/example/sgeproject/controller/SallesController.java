package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Salles; // Using Salles (plural)
import com.example.sgeproject.service.SallesService; // Using SallesService (plural)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gestionSalles") // Base mapping for admin salles operations
public class SallesController { // Controller name changed to SallesController

    private final SallesService sallesService; // Using SallesService

    public SallesController(SallesService sallesService) {
        this.sallesService = sallesService;
    }

    @GetMapping // Handles GET /admin/gestionSalles
    public String listSalles(Model model) {
        List<Salles> salles = sallesService.getAllSalles();
        model.addAttribute("salles", salles);
        return "gestionSalles"; // Renders src/main/resources/templates/gestionSalles.html
    }

    @GetMapping("/new") // Handles GET /admin/gestionSalles/new
    public String showCreateSallesForm(Model model) {
        model.addAttribute("salles", new Salles()); // Using Salles (plural)
        return "createSallesForm"; // Create this Thymeleaf template
    }

    @PostMapping("/save") // Handles POST /admin/gestionSalles/save
    public String saveSalles(@ModelAttribute Salles salles, RedirectAttributes redirectAttributes) { // Using Salles (plural)
        try {
            sallesService.saveSalles(salles); // Using Salles (plural)
            redirectAttributes.addFlashAttribute("successMessage", "Salles added successfully!");
            return "redirect:/admin/gestionSalles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding salle: " + e.getMessage());
            return "redirect:/admin/gestionSalles/new";
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /admin/gestionSalles/edit/{id}
    public String showEditSallesForm(@PathVariable Long id, Model model) {
        Salles salles = sallesService.getSallesById(id) // Using Salles (plural)
                .orElseThrow(() -> new ResourceNotFoundException("Salles not found with id: " + id));
        model.addAttribute("salles", salles);
        return "editSallesForm"; // Create this Thymeleaf template
    }

    @PostMapping("/update/{id}") // Handles POST /admin/gestionSalles/update/{id}
    public String updateSalles(@PathVariable Long id, @ModelAttribute Salles salles, RedirectAttributes redirectAttributes) { // Using Salles (plural)
        try {
            sallesService.updateSalles(id, salles); // Using Salles (plural)
            redirectAttributes.addFlashAttribute("successMessage", "Salles updated successfully!");
            return "redirect:/admin/gestionSalles";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/gestionSalles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating salle: " + e.getMessage());
            return "redirect:/admin/gestionSalles/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /admin/gestionSalles/delete/{id}
    public String deleteSalles(@PathVariable Long id, RedirectAttributes redirectAttributes) { // Using Salles (plural)
        try {
            sallesService.deleteSalles(id); // Using Salles (plural)
            redirectAttributes.addFlashAttribute("successMessage", "Salles deleted successfully!");
            return "redirect:/admin/gestionSalles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting salle: " + e.getMessage());
            return "redirect:/admin/gestionSalles";
        }
    }
}
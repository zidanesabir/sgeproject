package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Salles;
import com.example.sgeproject.service.SallesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gestionSalles") // Base mapping for admin salles operations
public class SallesController {

    private final SallesService sallesService;

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
        model.addAttribute("salles", new Salles());
        return "createSallesForm"; // Renders src/main/resources/templates/createSallesForm.html
    }

    @PostMapping("/save") // Handles POST /admin/gestionSalles/save
    public String saveSalles(@ModelAttribute Salles salles, RedirectAttributes redirectAttributes) {
        try {
            sallesService.saveSalles(salles);
            redirectAttributes.addFlashAttribute("successMessage", "Room added successfully!");
            return "redirect:/admin/gestionSalles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding room: " + e.getMessage());
            return "redirect:/admin/gestionSalles/new"; // Redirects back to form on error
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /admin/gestionSalles/edit/{id}
    public String showEditSallesForm(@PathVariable Long id, Model model) {
        Salles salles = sallesService.getSallesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        model.addAttribute("salles", salles);
        return "editSallesForm"; // Renders src/main/resources/templates/editSallesForm.html
    }

    @PostMapping("/update/{id}") // Handles POST /admin/gestionSalles/update/{id}
    public String updateSalles(@PathVariable Long id, @ModelAttribute Salles salles, RedirectAttributes redirectAttributes) {
        try {
            sallesService.updateSalles(id, salles);
            redirectAttributes.addFlashAttribute("successMessage", "Room updated successfully!");
            return "redirect:/admin/gestionSalles";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/gestionSalles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating room: " + e.getMessage());
            return "redirect:/admin/gestionSalles/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /admin/gestionSalles/delete/{id}
    public String deleteSalles(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sallesService.deleteSalles(id);
            redirectAttributes.addFlashAttribute("successMessage", "Room deleted successfully!");
            return "redirect:/admin/gestionSalles";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting room: " + e.getMessage());
            return "redirect:/admin/gestionSalles";
        }
    }
}
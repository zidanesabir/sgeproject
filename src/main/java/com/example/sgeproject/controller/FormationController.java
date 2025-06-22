package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Formation;
import com.example.sgeproject.service.FormationService; // Assuming FormationService exists
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gestionFormations") // Base mapping for admin formation operations
public class FormationController {

    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    @GetMapping // Handles GET /admin/gestionFormations
    public String listFormations(Model model) {
        List<Formation> formations = formationService.getAllFormations();
        model.addAttribute("formations", formations);
        return "gestionFormations"; // Renders src/main/resources/templates/gestionFormations.html
    }

    @GetMapping("/new") // Handles GET /admin/gestionFormations/new
    public String showCreateFormationForm(Model model) {
        model.addAttribute("formation", new Formation());
        return "createFormationForm"; // Create this Thymeleaf template
    }

    @PostMapping("/save") // Handles POST /admin/gestionFormations/save
    public String saveFormation(@ModelAttribute Formation formation, RedirectAttributes redirectAttributes) {
        try {
            formationService.saveFormation(formation);
            redirectAttributes.addFlashAttribute("successMessage", "Formation added successfully!");
            return "redirect:/admin/gestionFormations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding formation: " + e.getMessage());
            return "redirect:/admin/gestionFormations/new";
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /admin/gestionFormations/edit/{id}
    public String showEditFormationForm(@PathVariable Long id, Model model) {
        Formation formation = formationService.getFormationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + id));
        model.addAttribute("formation", formation);
        return "editFormationForm"; // Create this Thymeleaf template
    }

    @PostMapping("/update/{id}") // Handles POST /admin/gestionFormations/update/{id}
    public String updateFormation(@PathVariable Long id, @ModelAttribute Formation formation, RedirectAttributes redirectAttributes) {
        try {
            formationService.updateFormation(id, formation);
            redirectAttributes.addFlashAttribute("successMessage", "Formation updated successfully!");
            return "redirect:/admin/gestionFormations";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/gestionFormations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating formation: " + e.getMessage());
            return "redirect:/admin/gestionFormations/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /admin/gestionFormations/delete/{id}
    public String deleteFormation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            formationService.deleteFormation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Formation deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting formation: " + e.getMessage());
        }
        return "redirect:/admin/gestionFormations";
    }
}
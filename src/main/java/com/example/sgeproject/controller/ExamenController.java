package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.model.Module;
import com.example.sgeproject.model.SessionExamen;
import com.example.sgeproject.service.ExamenService;
import com.example.sgeproject.service.ModuleService; // Make sure this is injected
import com.example.sgeproject.service.SessionExamenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gestionExamens")
public class ExamenController {

    private final ExamenService examenService;
    private final ModuleService moduleService; // Injected
    private final SessionExamenService sessionExamenService;

    public ExamenController(ExamenService examenService, ModuleService moduleService, SessionExamenService sessionExamenService) {
        this.examenService = examenService;
        this.moduleService = moduleService; // Assigned
        this.sessionExamenService = sessionExamenService;
    }

    @GetMapping
    public String listExamens(Model model) {
        List<Examen> examens = examenService.getAllExamens();
        model.addAttribute("examens", examens);
        List<SessionExamen> sessions = sessionExamenService.getAllSessionExamens();
        model.addAttribute("sessions", sessions);
        return "gestionExamens";
    }

    @GetMapping("/new") // Handles GET /admin/gestionExamens/new
    public String showCreateExamenForm(Model model) {
        model.addAttribute("examen", new Examen());
        model.addAttribute("modules", moduleService.getAllModules()); // <<< CRITICAL: Add modules to model
        return "createExamenForm";
    }

    @PostMapping("/save") // Handles POST /admin/gestionExamens/save
    public String saveExamen(@ModelAttribute Examen examen, RedirectAttributes redirectAttributes) {
        try {
            // Need to load Module entity from DB based on ID from form
            if (examen.getModule() != null && examen.getModule().getId() != null) {
                Module module = moduleService.getModuleById(examen.getModule().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + examen.getModule().getId()));
                examen.setModule(module);
            }
            examenService.saveExamen(examen);
            redirectAttributes.addFlashAttribute("successMessage", "Examen added successfully!");
            return "redirect:/admin/gestionExamens";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding exam: " + e.getMessage());
            return "redirect:/admin/gestionExamens/new";
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /admin/gestionExamens/edit/{id}
    public String showEditExamenForm(@PathVariable Long id, Model model) {
        Examen examen = examenService.getExamenById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + id));
        model.addAttribute("examen", examen);
        model.addAttribute("modules", moduleService.getAllModules()); // <<< CRITICAL: Add modules to model for edit form
        return "editExamenForm";
    }

    @PostMapping("/update/{id}") // Handles POST /admin/gestionExamens/update/{id}
    public String updateExamen(@PathVariable Long id, @ModelAttribute Examen examen, RedirectAttributes redirectAttributes) {
        try {
            // Need to load Module entity from DB based on ID from form
            if (examen.getModule() != null && examen.getModule().getId() != null) {
                Module module = moduleService.getModuleById(examen.getModule().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + examen.getModule().getId()));
                examen.setModule(module);
            }
            examenService.updateExamen(id, examen);
            redirectAttributes.addFlashAttribute("successMessage", "Examen updated successfully!");
            return "redirect:/admin/gestionExamens";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/gestionExamens";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating exam: " + e.getMessage());
            return "redirect:/admin/gestionExamens/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /admin/gestionExamens/delete/{id}
    public String deleteExamen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examenService.deleteExamen(id);
            redirectAttributes.addFlashAttribute("successMessage", "Examen deleted successfully!");
            return "redirect:/admin/gestionExamens";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting exam: " + e.getMessage());
            return "redirect:/admin/gestionExamens";
        }
    }
}
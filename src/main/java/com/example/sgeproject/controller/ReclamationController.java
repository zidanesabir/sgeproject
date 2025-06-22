package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Reclamation;
import com.example.sgeproject.service.EtudiantService;
import com.example.sgeproject.service.ReclamationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reclamations") // Base mapping for reclamation operations
public class ReclamationController {

    private final ReclamationService reclamationService;
    private final EtudiantService etudiantService; // Injected for student-related data

    public ReclamationController(ReclamationService reclamationService, EtudiantService etudiantService) {
        this.reclamationService = reclamationService;
        this.etudiantService = etudiantService;
    }

    @GetMapping // Handles GET /reclamations
    public String listReclamations(Model model) {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        model.addAttribute("reclamations", reclamations);
        return "reclamations"; // Renders src/main/resources/templates/reclamations.html
    }

    @GetMapping("/submit") // Handles GET /reclamations/submit
    public String showSubmitReclamationForm(Model model) {
        model.addAttribute("reclamation", new Reclamation());
        // You'll need to populate notes specific to the logged-in student here.
        // For now, providing all students for form example (will be filtered later)
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants); // For selecting student if needed (in admin/prof context)
        // You should fetch notes for the currently logged-in student, e.g.:
        // List<Note> studentNotes = noteService.getNotesByEtudiantId(loggedInStudentId);
        // model.addAttribute("studentNotes", studentNotes);
        return "reclamations-submit"; // Renders src/main/resources/templates/reclamations-submit.html
    }

    @PostMapping("/submit") // Handles POST /reclamations/submit
    public String submitReclamation(@ModelAttribute Reclamation reclamation, RedirectAttributes redirectAttributes) {
        try {
            // Need to link Etudiant based on logged-in user, and Note based on form selection
            // Example:
            // reclamation.setEtudiant(etudiantService.getEtudiantById(loggedInEtudiantId).orElse(null));
            reclamationService.saveReclamation(reclamation);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation submitted successfully!");
            return "redirect:/reclamations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting reclamation: " + e.getMessage());
            return "redirect:/reclamations/submit";
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /reclamations/edit/{id}
    public String showEditReclamationForm(@PathVariable Long id, Model model) {
        Reclamation reclamation = reclamationService.getReclamationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        model.addAttribute("reclamation", reclamation);
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants); // For selecting student if needed
        return "reclamations-edit"; // Renders src/main/resources/templates/reclamations-edit.html
    }

    @PostMapping("/update/{id}") // Handles POST /reclamations/update/{id}
    public String updateReclamation(@PathVariable Long id, @ModelAttribute Reclamation reclamation, RedirectAttributes redirectAttributes) {
        try {
            reclamationService.updateReclamation(id, reclamation);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation updated successfully!");
            return "redirect:/reclamations";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reclamations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating reclamation: " + e.getMessage());
            return "redirect:/reclamations/edit/" + id;
        }
    }

    @PostMapping("/approve/{id}") // Handles POST /reclamations/approve/{id}
    public String approveReclamation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reclamationService.approveReclamation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving reclamation: " + e.getMessage());
        }
        return "redirect:/reclamations";
    }

    @PostMapping("/reject/{id}") // Handles POST /reclamations/reject/{id}
    public String rejectReclamation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reclamationService.rejectReclamation(id);
            redirectAttributes.addFlashAttribute("errorMessage", "Reclamation rejected!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting reclamation: " + e.getMessage());
        }
        return "redirect:/reclamations";
    }

    @PostMapping("/delete/{id}") // Handles POST /reclamations/delete/{id}
    public String deleteReclamation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reclamationService.deleteReclamation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting reclamation: " + e.getMessage());
        }
        return "redirect:/reclamations";
    }
}
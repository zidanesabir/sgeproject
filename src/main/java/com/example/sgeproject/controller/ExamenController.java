package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.model.Module;
import com.example.sgeproject.model.SessionExamen;
import com.example.sgeproject.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gestionExamens") // Base mapping for admin exam operations
public class ExamenController {

    private final ExamenService examenService;
    private final ModuleService moduleService;
    private final SessionExamenService sessionExamenService;
    private final SallesService sallesService; // Inject SallesService
    private final ProfesseurService professeurService; // Inject ProfesseurService


    public ExamenController(ExamenService examenService,
                            ModuleService moduleService,
                            SessionExamenService sessionExamenService,
                            SallesService sallesService,
                            ProfesseurService professeurService) {
        this.examenService = examenService;
        this.moduleService = moduleService;
        this.sessionExamenService = sessionExamenService;
        this.sallesService = sallesService;
        this.professeurService = professeurService;
    }

    // Handles GET /admin/gestionExamens (List all exams and sessions)
    @GetMapping
    public String listExamens(Model model) {
        List<Examen> examens = examenService.getAllExamens(); // Should use findAllWithDetails
        model.addAttribute("examens", examens);

        // Ensure 'sessions' list is added to the model AND is never null for the template
        List<SessionExamen> sessions = sessionExamenService.getAllSessionExamens(); // This should get a List (possibly empty)
        if (sessions == null) {
            sessions = new java.util.ArrayList<>(); // Fallback to empty list if service somehow returns null
        }
        model.addAttribute("sessions", sessions); // Provide this to the template for sessions.isEmpty()

        return "gestionExamens"; // Renders src/main/resources/templates/gestionExamens.html
    }

    // Handles GET /admin/gestionExamens/new (Display form to add a new exam)
    @GetMapping("/new")
    public String showCreateExamenForm(Model model) {
        model.addAttribute("examen", new Examen());
        model.addAttribute("modules", moduleService.getAllModules()); // For dropdown
        return "createExamenForm"; // Renders src/main/resources/templates/createExamenForm.html
    }

    // Handles POST /admin/gestionExamens/save (Process adding a new exam)
    @PostMapping("/save")
    public String saveExamen(@ModelAttribute Examen examen, RedirectAttributes redirectAttributes) {
        try {
            // Ensure the associated Module is correctly linked before saving
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

    // Handles GET /admin/gestionExamens/edit/{id} (Display form to edit an exam)
    @GetMapping("/edit/{id}")
    public String showEditExamenForm(@PathVariable Long id, Model model) {
        Examen examen = examenService.getExamenById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + id));
        model.addAttribute("examen", examen);
        model.addAttribute("modules", moduleService.getAllModules()); // For dropdown
        return "editExamenForm"; // Renders src/main/resources/templates/editExamenForm.html
    }

    // Handles POST /admin/gestionExamens/update/{id} (Process updating an exam)
    @PostMapping("/update/{id}")
    public String updateExamen(@PathVariable Long id, @ModelAttribute Examen examen, RedirectAttributes redirectAttributes) {
        try {
            // Ensure associated Module is correctly linked before updating
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

    // Handles POST /admin/gestionExamens/delete/{id} (Delete an exam)
    @PostMapping("/delete/{id}")
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

    // --- Exam Session Management (Nested within ExamenController for simplicity, or could be a separate SessionExamenController) ---

    // Handles GET /admin/gestionExamens/sessions/new (Display form to add a new session)
    @GetMapping("/sessions/new")
    public String showCreateSessionExamenForm(Model model) {
        model.addAttribute("sessionExamen", new SessionExamen());
        model.addAttribute("examensList", examenService.getAllExamens()); // Provide all exams for dropdown
        model.addAttribute("sallesList", sallesService.getAllSalles()); // Provide all salles for dropdown
        model.addAttribute("professeursList", professeurService.getAllProfesseurs()); // Provide all professors for dropdown
        return "createSessionExamenForm"; // Create this Thymeleaf template
    }

    // Handles POST /admin/gestionExamens/sessions/save (Process adding a new session)
    @PostMapping("/sessions/save")
    public String saveSessionExamen(@ModelAttribute SessionExamen sessionExamen, RedirectAttributes redirectAttributes) {
        try {
            // Ensure associated Examen, Salle, and Professeur (supervisors) are correctly linked
            sessionExamenService.saveSessionExamen(sessionExamen);
            redirectAttributes.addFlashAttribute("successMessage", "Session added successfully!");
            return "redirect:/admin/gestionExamens"; // Redirect back to the main exams/sessions list
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding session: " + e.getMessage());
            return "redirect:/admin/gestionExamens/sessions/new";
        }
    }

    // Handles GET /admin/gestionExamens/sessions/edit/{id} (Display form to edit a session)
    @GetMapping("/sessions/edit/{id}")
    public String showEditSessionExamenForm(@PathVariable Long id, Model model) {
        SessionExamen sessionExamen = sessionExamenService.getSessionExamenById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SessionExamen not found with id: " + id));
        model.addAttribute("sessionExamen", sessionExamen);
        model.addAttribute("examensList", examenService.getAllExamens());
        model.addAttribute("sallesList", sallesService.getAllSalles());
        model.addAttribute("professeursList", professeurService.getAllProfesseurs());
        return "editSessionExamenForm"; // Create this Thymeleaf template
    }

    // Handles POST /admin/gestionExamens/sessions/update/{id} (Process updating a session)
    @PostMapping("/sessions/update/{id}")
    public String updateSessionExamen(@PathVariable Long id, @ModelAttribute SessionExamen sessionExamen, RedirectAttributes redirectAttributes) {
        try {
            sessionExamenService.updateSessionExamen(id, sessionExamen);
            redirectAttributes.addFlashAttribute("successMessage", "Session updated successfully!");
            return "redirect:/admin/gestionExamens";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/gestionExamens";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating session: " + e.getMessage());
            return "redirect:/admin/gestionExamens/sessions/edit/" + id;
        }
    }

    // Handles POST /admin/gestionExamens/sessions/delete/{id} (Delete a session)
    @PostMapping("/sessions/delete/{id}")
    public String deleteSessionExamen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sessionExamenService.deleteSessionExamen(id);
            redirectAttributes.addFlashAttribute("successMessage", "Session deleted successfully!");
            return "redirect:/admin/gestionExamens";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting session: " + e.getMessage());
            return "redirect:/admin/gestionExamens";
        }
    }
}
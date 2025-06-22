package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.model.Reclamation;
import com.example.sgeproject.service.EtudiantService;
import com.example.sgeproject.service.NoteService;
import com.example.sgeproject.service.ProfesseurService;
import com.example.sgeproject.service.ReclamationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/reclamations") // Base mapping for reclamation operations
public class ReclamationController {

    private final ReclamationService reclamationService;
    private final EtudiantService etudiantService;
    private final NoteService noteService; // <<< CRITICAL: NoteService injected for studentNotes dropdown
    private final ProfesseurService professeurService; // <<< CRITICAL: ProfesseurService injected for prof checks

    public ReclamationController(ReclamationService reclamationService, EtudiantService etudiantService,
                                 NoteService noteService, ProfesseurService professeurService) { // <<< Updated constructor
        this.reclamationService = reclamationService;
        this.etudiantService = etudiantService;
        this.noteService = noteService; // Assign
        this.professeurService = professeurService; // Assign
    }

    /**
     * Handles GET requests to display reclamations list based on user role.
     * ADMIN: sees all reclamations.
     * PROFESSOR: redirected to a professor-specific view (handled in ProfesseurController).
     * ETUDIANT: redirected to a student-specific view (handled in EtudiantController).
     * @param model The Model to pass data to the view.
     * @param session The HttpSession for user role.
     * @return The name of the Thymeleaf template to render or redirect.
     */
    @GetMapping // Handles GET /reclamations
    public String listReclamations(Model model, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");

        if (loggedInUserId == null || loggedInUserRole == null) {
            return "redirect:/login?error=unauthorized";
        }

        // Redirect based on role to their specific reclamation views
        if ("ETUDIANT".equals(loggedInUserRole)) {
            return "redirect:/etudiant/reclamations"; // Student's own reclamations
        } else if ("PROFESSOR".equals(loggedInUserRole)) {
            return "redirect:/professor/reclamations"; // Professor's own relevant reclamations
        }

        // Default: If logged in as ADMIN, show all reclamations
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        model.addAttribute("reclamations", reclamations);
        model.addAttribute("isAdminView", true); // Flag for admin view in template
        model.addAttribute("loggedInUserId", loggedInUserId); // For conditional display in template

        return "reclamations"; // Renders src/main/resources/templates/reclamations.html (general list)
    }

    /**
     * Handles GET requests to display form to submit a new reclamation.
     * This is the STUDENT's view.
     * @param model The Model to pass data to the view.
     * @param session The HttpSession to get logged-in student ID.
     * @return The name of the Thymeleaf template to render.
     */
    @GetMapping("/submit")
    public String showSubmitReclamationForm(Model model, HttpSession session) {
        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");

        if (loggedInEtudiantId == null || !"ETUDIANT".equals(loggedInUserRole)) {
            return "redirect:/login?error=unauthorized"; // Only students can submit
        }

        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found."));

        model.addAttribute("reclamation", new Reclamation());
        model.addAttribute("etudiant", etudiant); // Add the student object

        // Provide notes specific to this student for the dropdown in the form
        List<Note> studentNotes = noteService.getNotesByEtudiant(etudiant);
        model.addAttribute("studentNotes", studentNotes); // <<< CRITICAL: Add notes for dropdown

        return "reclamations-submit"; // A specific form template for submitting
    }

    /**
     * Process submitting a new reclamation.
     * Only Students can create their own.
     * @param reclamation The Reclamation object from the form.
     * @param redirectAttributes For flash messages.
     * @param session The HttpSession to get logged-in student ID.
     * @return Redirect URL.
     */
    @PostMapping("/submit")
    public String submitReclamation(@ModelAttribute Reclamation reclamation, RedirectAttributes redirectAttributes, HttpSession session) {
        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");

        if (loggedInEtudiantId == null || !"ETUDIANT".equals(loggedInUserRole)) {
            return "redirect:/login?error=unauthorized";
        }

        try {
            // Ensure reclamation is linked to the logged-in student
            Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found for reclamation submission."));
            reclamation.setEtudiant(etudiant);

            // Ensure the note selected belongs to this student and is valid
            if (reclamation.getNote() != null && reclamation.getNote().getId() != null) {
                Note note = noteService.getNoteById(reclamation.getNote().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + reclamation.getNote().getId()));
                if (!note.getEtudiant().getId().equals(loggedInEtudiantId)) {
                    throw new IllegalArgumentException("You can only submit reclamations for your own notes.");
                }
                reclamation.setNote(note);
            } else {
                throw new IllegalArgumentException("A specific note must be selected for the reclamation.");
            }

            reclamationService.saveReclamation(reclamation);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation submitted successfully!");
            return "redirect:/etudiant/reclamations"; // Redirect to student's view of reclamations
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting reclamation: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/reclamations/submit"; // Redirect back to submit form on error
        }
    }

    /**
     * Display form to edit a reclamation.
     * Accessible by Students (for their own pending) or Admin/Professor (any).
     * @param id The ID of the reclamation.
     * @param model The Model.
     * @param session The HttpSession.
     * @return Template name.
     */
    @GetMapping("/edit/{id}")
    public String showEditReclamationForm(@PathVariable Long id, Model model, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");
        if (loggedInUserId == null || loggedInUserRole == null) { return "redirect:/login?error=unauthorized"; }

        Reclamation reclamation = reclamationService.getReclamationById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));

        // Authorization check for viewing/editing
        if ("ETUDIANT".equals(loggedInUserRole) && !reclamation.getEtudiant().getId().equals(loggedInUserId)) {
            return "redirect:/etudiant/reclamations?error=unauthorized"; // Not their reclamation
        }
        if ("ETUDIANT".equals(loggedInUserRole) && !"Pending".equals(reclamation.getStatut())) {
            return "redirect:/etudiant/reclamations?error=cannotEditApproved"; // Student can't edit approved/rejected
        }

        model.addAttribute("reclamation", reclamation);
        model.addAttribute("etudiants", etudiantService.getAllEtudiants()); // For dropdown if admin/prof editing
        model.addAttribute("studentNotes", noteService.getNotesByEtudiant(reclamation.getEtudiant())); // Notes specific to the reclamation's student

        // Flags for template to show/hide fields/buttons
        model.addAttribute("isAdminView", "ADMIN".equals(loggedInUserRole));
        model.addAttribute("isProfessorView", "PROFESSOR".equals(loggedInUserRole));
        model.addAttribute("loggedInUserId", loggedInUserId);


        return "reclamations-edit"; // A specific form template for editing
    }

    /**
     * Process updating a reclamation.
     * Accessible by Students (their own pending) or Admin/Professor (any).
     * @param id The ID of the reclamation.
     * @param reclamation The updated Reclamation object from the form.
     * @param redirectAttributes For flash messages.
     * @param session The HttpSession.
     * @return Redirect URL.
     */
    @PostMapping("/update/{id}")
    public String updateReclamation(@PathVariable Long id, @ModelAttribute Reclamation reclamation, RedirectAttributes redirectAttributes, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");
        if (loggedInUserId == null || loggedInUserRole == null) { return "redirect:/login?error=unauthorized"; }

        try {
            Reclamation existingReclamation = reclamationService.getReclamationById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));

            // Authorization Check: Only owner/Admin/Prof can update. Student only if Pending.
            if ("ETUDIANT".equals(loggedInUserRole) && !existingReclamation.getEtudiant().getId().equals(loggedInUserId)) {
                throw new IllegalArgumentException("You can only update your own reclamations.");
            }
            if ("ETUDIANT".equals(loggedInUserRole) && !"Pending".equals(existingReclamation.getStatut())) {
                throw new IllegalArgumentException("Students cannot modify approved/rejected reclamations.");
            }

            // Ensure linked Etudiant and Note are valid and belong to original reclamation's student
            Etudiant managedEtudiant = etudiantService.getEtudiantById(reclamation.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + reclamation.getEtudiant().getId()));
            existingReclamation.setEtudiant(managedEtudiant);

            if (reclamation.getNote() != null && reclamation.getNote().getId() != null) {
                Note note = noteService.getNoteById(reclamation.getNote().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Note not found with ID: " + reclamation.getNote().getId()));
                if (!note.getEtudiant().getId().equals(existingReclamation.getEtudiant().getId())) { // Note must belong to same student
                    throw new IllegalArgumentException("Selected note does not belong to the reclamation's student.");
                }
                existingReclamation.setNote(note);
            } else {
                throw new IllegalArgumentException("A specific note must be selected for the reclamation.");
            }

            existingReclamation.setRaison(reclamation.getRaison()); // Only student can change reason
            // Status update only allowed by Admin/Professor
            if ("ADMIN".equals(loggedInUserRole) || "PROFESSOR".equals(loggedInUserRole)) {
                existingReclamation.setStatut(reclamation.getStatut()); // Admin/Prof can change status
            }
            // else student cannot change status

            reclamationService.updateReclamation(id, existingReclamation);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation updated successfully!");

            if ("ETUDIANT".equals(loggedInUserRole)) {
                return "redirect:/etudiant/reclamations";
            } else { // Admin or Professor
                return "redirect:/reclamations";
            }
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reclamations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating reclamation: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/reclamations/edit/" + id;
        }
    }

    /**
     * Approve a reclamation.
     * Only Admin or Professor can perform this action.
     * @param id The ID of the reclamation.
     * @param redirectAttributes For flash messages.
     * @param session The HttpSession.
     * @return Redirect URL.
     */
    @PostMapping("/approve/{id}")
    public String approveReclamation(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");
        if (loggedInUserId == null || (!"ADMIN".equals(loggedInUserRole) && !"PROFESSOR".equals(loggedInUserRole))) {
            return "redirect:/login?error=unauthorized";
        }
        try {
            Reclamation reclamation = reclamationService.getReclamationById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found."));
            // Additional check: If professor, ensure they teach the module of the note in reclamation
            // Get the specific module ID from the note in the reclamation
            Long reclamationModuleProfesseurId = reclamation.getNote() != null &&
                    reclamation.getNote().getExamen() != null &&
                    reclamation.getNote().getExamen().getModule() != null &&
                    reclamation.getNote().getExamen().getModule().getProfesseur() != null
                    ? reclamation.getNote().getExamen().getModule().getProfesseur().getId()
                    : null;

            if ("PROFESSOR".equals(loggedInUserRole) &&
                    (reclamationModuleProfesseurId == null || !reclamationModuleProfesseurId.equals(loggedInUserId))) {
                throw new IllegalArgumentException("You can only approve/reject reclamations for your own modules.");
            }

            reclamationService.approveReclamation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving reclamation: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/reclamations"; // Redirect to general list (admin/prof view)
    }

    /**
     * Reject a reclamation.
     * Only Admin or Professor can perform this action.
     * @param id The ID of the reclamation.
     * @param redirectAttributes For flash messages.
     * @param session The HttpSession.
     * @return Redirect URL.
     */
    @PostMapping("/reject/{id}")
    public String rejectReclamation(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");
        if (loggedInUserId == null || (!"ADMIN".equals(loggedInUserRole) && !"PROFESSOR".equals(loggedInUserRole))) {
            return "redirect:/login?error=unauthorized";
        }
        try {
            Reclamation reclamation = reclamationService.getReclamationById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found."));

            Long reclamationModuleProfesseurId = reclamation.getNote() != null &&
                    reclamation.getNote().getExamen() != null &&
                    reclamation.getNote().getExamen().getModule() != null &&
                    reclamation.getNote().getExamen().getModule().getProfesseur() != null
                    ? reclamation.getNote().getExamen().getModule().getProfesseur().getId()
                    : null;

            if ("PROFESSOR".equals(loggedInUserRole) &&
                    (reclamationModuleProfesseurId == null || !reclamationModuleProfesseurId.equals(loggedInUserId))) {
                throw new IllegalArgumentException("You can only approve/reject reclamations for your own modules.");
            }

            reclamationService.rejectReclamation(id);
            redirectAttributes.addFlashAttribute("errorMessage", "Reclamation rejected!"); // Use errorMessage for rejection feedback
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting reclamation: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/reclamations"; // Redirect to general list (admin/prof view)
    }

    /**
     * Delete a reclamation.
     * Accessible by Students (their own pending) or Admin (any).
     * @param id The ID of the reclamation.
     * @param redirectAttributes For flash messages.
     * @param session The HttpSession.
     * @return Redirect URL.
     */
    @PostMapping("/delete/{id}")
    public String deleteReclamation(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Long loggedInUserId = (Long) session.getAttribute("loggedInUserId");
        String loggedInUserRole = (String) session.getAttribute("loggedInUserRole");
        if (loggedInUserId == null) { return "redirect:/login?error=unauthorized"; }

        try {
            Reclamation reclamationToDelete = reclamationService.getReclamationById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));

            // Authorization Check: Student can delete ONLY their own (if pending), Admin can delete any.
            if ("ETUDIANT".equals(loggedInUserRole)) {
                if (!reclamationToDelete.getEtudiant().getId().equals(loggedInUserId)) {
                    throw new IllegalArgumentException("You can only delete your own reclamations.");
                }
                if (!"Pending".equals(reclamationToDelete.getStatut())) {
                    throw new IllegalArgumentException("Students cannot delete approved/rejected reclamations.");
                }
            } else if ("PROFESSOR".equals(loggedInUserRole)) {
                // Professors cannot delete reclamations directly, only approve/reject
                throw new IllegalArgumentException("Professors cannot delete reclamations.");
            }
            // Admin can delete any

            reclamationService.deleteReclamation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reclamation deleted successfully!");

            if ("ETUDIANT".equals(loggedInUserRole)) {
                return "redirect:/etudiant/reclamations";
            } else { // Admin or Professor (though Professor delete is blocked above)
                return "redirect:/reclamations";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting reclamation: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/reclamations";
        }
    }
}
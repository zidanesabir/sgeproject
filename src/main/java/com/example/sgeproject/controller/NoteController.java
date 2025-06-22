package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.service.EtudiantService;
import com.example.sgeproject.service.ExamenService;
import com.example.sgeproject.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/notes") // Base mapping for note operations
public class NoteController {

    private final NoteService noteService;
    private final EtudiantService etudiantService;
    private final ExamenService examenService;

    public NoteController(NoteService noteService, EtudiantService etudiantService, ExamenService examenService) {
        this.noteService = noteService;
        this.etudiantService = etudiantService;
        this.examenService = examenService;
    }

    // Displays all notes (if accessed directly, but usually handled by ProfesseurController)
    @GetMapping
    public String listNotes(Model model) {
        List<Note> notes = noteService.getAllNotes(); // Corrected method name (uses JOIN FETCH now)
        model.addAttribute("notes", notes);
        return "gestionNotes"; // Thymeleaf template name
    }

    // --- CRITICAL FIX: Ensure 'note' object and dropdown lists are added for 'new' form ---
    @GetMapping("/new") // Handles GET /notes/new
    public String showCreateNoteForm(Model model) {
        model.addAttribute("note", new Note()); // <<< Add an empty Note object for form binding
        // Pre-populate date fields if desired
        // model.addAttribute("note", new Note(null, null, LocalDate.now(), null, null, false, null, null)); // Example for specific constructor

        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants); // For Student dropdown
        List<Examen> examens = examenService.getAllExamens();
        model.addAttribute("examens", examens);     // For Exam dropdown
        return "createNoteForm"; // A new Thymeleaf template for creating notes
    }
    // -------------------------------------------------------------------------------------

    @PostMapping("/save") // Handles POST /notes/save
    public String saveNote(@ModelAttribute Note note, RedirectAttributes redirectAttributes) {
        try {
            // Set dateModification and estValidee defaults if not set by form
            if (note.getDateSaisie() == null) {
                note.setDateSaisie(LocalDate.now());
            }
            note.setDateModification(LocalDateTime.now());
            // note.setEstValidee(false); // Only set if you want it always false initially

            // Need to ensure Etudiant and Examen objects are fully loaded if only IDs are passed from form
            if (note.getEtudiant() != null && note.getEtudiant().getId() != null) {
                Etudiant etudiant = etudiantService.getEtudiantById(note.getEtudiant().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + note.getEtudiant().getId()));
                note.setEtudiant(etudiant);
            }
            if (note.getExamen() != null && note.getExamen().getId() != null) {
                Examen examen = examenService.getExamenById(note.getExamen().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + note.getExamen().getId()));
                note.setExamen(examen);
            }
            noteService.saveNote(note);
            redirectAttributes.addFlashAttribute("successMessage", "Note saved successfully!");
            return "redirect:/professor/gestionNotes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving note: " + e.getMessage());
            e.printStackTrace(); // Keep for debugging
            return "redirect:/professor/gestionNotes"; // Redirect back to list page on error
        }
    }

    // --- CRITICAL FIX: Ensure 'note' object and dropdown lists are added for 'edit' form ---
    @GetMapping("/edit/{id}") // Handles GET /notes/edit/{id}
    public String showEditNoteForm(@PathVariable Long id, Model model) {
        Note note = noteService.getNoteById(id) // Corrected method name
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        model.addAttribute("note", note); // <<< Add the fetched Note object for form binding

        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants); // For Student dropdown
        List<Examen> examens = examenService.getAllExamens();
        model.addAttribute("examens", examens);     // For Exam dropdown
        return "editNoteForm"; // A new Thymeleaf template for editing notes
    }
    // -------------------------------------------------------------------------------------

    @PostMapping("/update/{id}") // Handles POST /notes/update/{id}
    public String updateNote(@PathVariable Long id, @ModelAttribute Note note, RedirectAttributes redirectAttributes) {
        try {
            // Set modification date automatically
            note.setDateModification(LocalDateTime.now());

            if (note.getEtudiant() != null && note.getEtudiant().getId() != null) {
                Etudiant etudiant = etudiantService.getEtudiantById(note.getEtudiant().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + note.getEtudiant().getId()));
                note.setEtudiant(etudiant);
            }
            if (note.getExamen() != null && note.getExamen().getId() != null) {
                Examen examen = examenService.getExamenById(note.getExamen().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + note.getExamen().getId()));
                note.setExamen(examen);
            }
            noteService.updateNote(id, note);
            redirectAttributes.addFlashAttribute("successMessage", "Note updated successfully!");
            return "redirect:/professor/gestionNotes";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/professor/gestionNotes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating note: " + e.getMessage());
            e.printStackTrace(); // Keep for debugging
            return "redirect:/professor/gestionNotes/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /notes/delete/{id}
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("successMessage", "Note deleted successfully!");
            return "redirect:/professor/gestionNotes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting note: " + e.getMessage());
            return "redirect:/professor/gestionNotes";
        }
    }

    // This method is used by 'relevésNotes.html' if it's retrieving notes for a specific student
    // (This is likely in EtudiantController now, but if you keep a NoteController version, ensure it works)
    @GetMapping("/etudiant/{etudiantId}")
    public String getNotesForEtudiant(@PathVariable Long etudiantId, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + etudiantId));
        List<Note> notes = noteService.getNotesByEtudiant(etudiant);
        model.addAttribute("notes", notes);
        model.addAttribute("etudiant", etudiant);
        return "relevésNotes";
    }
}
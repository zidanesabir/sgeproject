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

    @GetMapping // Handles GET /notes
    public String listNotes(Model model) {
        List<Note> notes = noteService.getAllNotes();
        model.addAttribute("notes", notes);
        return "gestionNotes"; // Renders src/main/resources/templates/gestionNotes.html
    }

    @GetMapping("/new") // Handles GET /notes/new
    public String showCreateNoteForm(Model model) {
        model.addAttribute("note", new Note());
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants);
        List<Examen> examens = examenService.getAllExamens();
        model.addAttribute("examens", examens);
        return "createNoteForm"; // Create this Thymeleaf template
    }

    @PostMapping("/save") // Handles POST /notes/save
    public String saveNote(@ModelAttribute Note note, RedirectAttributes redirectAttributes) {
        try {
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
            return "redirect:/notes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving note: " + e.getMessage());
            return "redirect:/notes/new";
        }
    }

    @GetMapping("/edit/{id}") // Handles GET /notes/edit/{id}
    public String showEditNoteForm(@PathVariable Long id, Model model) {
        Note note = noteService.getNoteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        model.addAttribute("note", note);
        List<Etudiant> etudiants = etudiantService.getAllEtudiants();
        model.addAttribute("etudiants", etudiants);
        List<Examen> examens = examenService.getAllExamens();
        model.addAttribute("examens", examens);
        return "editNoteForm"; // Create this Thymeleaf template
    }

    @PostMapping("/update/{id}") // Handles POST /notes/update/{id}
    public String updateNote(@PathVariable Long id, @ModelAttribute Note note, RedirectAttributes redirectAttributes) {
        try {
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
            return "redirect:/notes";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/notes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating note: " + e.getMessage());
            return "redirect:/notes/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}") // Handles POST /notes/delete/{id}
    public String deleteNote(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noteService.deleteNote(id);
            redirectAttributes.addFlashAttribute("successMessage", "Note deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting note: " + e.getMessage());
        }
        return "redirect:/notes";
    }

    @GetMapping("/etudiant/{etudiantId}") // Handles GET /notes/etudiant/{etudiantId}
    public String getNotesForEtudiant(@PathVariable Long etudiantId, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + etudiantId));
        List<Note> notes = noteService.getNotesByEtudiant(etudiant);
        model.addAttribute("notes", notes);
        model.addAttribute("etudiant", etudiant);
        return "relevésNotes"; // Renders src/main/resources/templates/relevésNotes.html
    }
}
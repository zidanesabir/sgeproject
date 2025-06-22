package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.*;
import com.example.sgeproject.model.Module;
import com.example.sgeproject.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/professor")
public class ProfesseurController {

    private final ProfesseurService professeurService;
    private final ModuleService moduleService;
    private final NoteService noteService;
    private final SessionExamenService sessionExamenService;
    private final EtudiantService etudiantService;
    private final ExamenService examenService;

    public ProfesseurController(ProfesseurService professeurService,
                                ModuleService moduleService,
                                NoteService noteService,
                                SessionExamenService sessionExamenService,
                                EtudiantService etudiantService,
                                ExamenService examenService) {
        this.professeurService = professeurService;
        this.moduleService = moduleService;
        this.noteService = noteService;
        this.sessionExamenService = sessionExamenService;
        this.etudiantService = etudiantService;
        this.examenService = examenService;
    }

    @GetMapping("/gestionNotes")
    public String showGestionNotes(Model model, HttpSession session,
                                   @RequestParam(value = "moduleId", required = false) Long moduleId) {
        Long loggedInProfesseurId = (Long) session.getAttribute("loggedInUserId"); // Get ID from session

        // --- CRITICAL FIX: If no user ID in session, redirect to login ---
        if (loggedInProfesseurId == null) {
            // This means the user is not "logged in" for this unsecured application,
            // or their session expired. Redirect them to log in.
            return "redirect:/login?error=sessionExpired"; // Redirect with an error message
        }
        // -----------------------------------------------------------------

        Professeur professeur = professeurService.getProfesseurById(loggedInProfesseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Professeur not found with ID: " + loggedInProfesseurId));

        List<Module> modulesTaught = moduleService.getAllModules().stream()
                .filter(m -> m.getProfesseur() != null && m.getProfesseur().getId().equals(loggedInProfesseurId))
                .collect(Collectors.toList());

        List<Note> allNotesFromDb = noteService.getAllNotes();

        List<Note> notesToDisplay;
        List<Note> notesRelevantToProfessorModules = allNotesFromDb.stream()
                .filter(note -> note.getExamen() != null &&
                        note.getExamen().getModule() != null &&
                        modulesTaught.stream().anyMatch(mt -> mt.getId().equals(note.getExamen().getModule().getId())))
                .collect(Collectors.toList());

        if (moduleId != null) {
            notesToDisplay = notesRelevantToProfessorModules.stream()
                    .filter(note -> note.getExamen().getModule().getId().equals(moduleId))
                    .collect(Collectors.toList());
        } else {
            notesToDisplay = notesRelevantToProfessorModules;
        }

        Set<Etudiant> studentsForDropdownSet = new HashSet<>();
        List<Examen> examsForTaughtModules = examenService.getAllExamens().stream()
                .filter(e -> e.getModule() != null &&
                        modulesTaught.stream().anyMatch(mt -> mt.getId().equals(e.getModule().getId())))
                .collect(Collectors.toList());

        allNotesFromDb.stream()
                .filter(note -> note.getEtudiant() != null &&
                        examsForTaughtModules.stream().anyMatch(exam -> exam.getId().equals(note.getExamen().getId())))
                .map(Note::getEtudiant)
                .forEach(studentsForDropdownSet::add);

        List<Etudiant> etudiantsForDropdown = studentsForDropdownSet.stream()
                .sorted(Comparator.comparing(Etudiant::getNom))
                .collect(Collectors.toList());

        List<Examen> examsForDropdown = examsForTaughtModules.stream()
                .sorted(Comparator.comparing(Examen::getDateExamen).reversed())
                .collect(Collectors.toList());

        model.addAttribute("professeur", professeur);
        model.addAttribute("modules", modulesTaught);
        model.addAttribute("notes", notesToDisplay);

        model.addAttribute("note", new Note());
        model.addAttribute("etudiants", etudiantsForDropdown);
        model.addAttribute("examens", examsForDropdown);

        return "gestionNotes";
    }

    @GetMapping("/planningExamens")
    public String showPlanningExamens(Model model, HttpSession session) {
        Long loggedInProfesseurId = (Long) session.getAttribute("loggedInUserId");
        if (loggedInProfesseurId == null) {
            return "redirect:/login?error=sessionExpired"; // Redirect if not logged in
        }

        Professeur professeur = professeurService.getProfesseurById(loggedInProfesseurId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Professeur not found with ID: " + loggedInProfesseurId));

        List<SessionExamen> allSessions = sessionExamenService.getAllSessionExamens();
        List<SessionExamen> supervisedSessions = allSessions.stream()
                .filter(s -> s.getSuperviseurs() != null &&
                        s.getSuperviseurs().stream().anyMatch(p -> p.getId().equals(loggedInProfesseurId)))
                .collect(Collectors.toList());
        model.addAttribute("professeur", professeur);
        model.addAttribute("supervisedSessions", supervisedSessions);

        return "planningExamens";
    }
}
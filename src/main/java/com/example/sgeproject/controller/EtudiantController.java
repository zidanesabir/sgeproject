package com.example.sgeproject.controller;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.model.Reclamation;
import com.example.sgeproject.model.SessionExamen;
import com.example.sgeproject.service.EtudiantService;
import com.example.sgeproject.service.NoteService;
import com.example.sgeproject.service.ReclamationService;
import com.example.sgeproject.service.SessionExamenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
//
//import com.example.sgeproject.exception.ResourceNotFoundException;
//import com.example.sgeproject.model.Etudiant;
//import com.example.sgeproject.model.Note;
//import com.example.sgeproject.model.Reclamation;
//import com.example.sgeproject.model.SessionExamen;
//import com.example.sgeproject.service.EtudiantService;
//import com.example.sgeproject.service.NoteService;
//import com.example.sgeproject.service.ReclamationService;
//import com.example.sgeproject.service.SessionExamenService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur gérant les requêtes liées aux étudiants dans l'application SGE.
 * Ce contrôleur gère l'affichage des relevés de notes, des réclamations et du planning des examens.
 */
// Contrôleur pour la gestion des étudiants
@Controller
@RequestMapping("/etudiant")
public class EtudiantController {

    // Services injectés pour la gestion des données
    // Service pour la gestion des étudiants
    private final EtudiantService etudiantService;          // Service pour la gestion des étudiants
    // Service pour la gestion des notes
    private final NoteService noteService;                  // Service pour la gestion des notes
    // Service pour la gestion des réclamations
    private final ReclamationService reclamationService;    // Service pour la gestion des réclamations
    // Service pour la gestion des sessions d'examen
    private final SessionExamenService sessionExamenService; // Service pour la gestion des sessions d'examen

    // Constructeur avec injection des dépendances
    /**
     * Constructeur pour l'initialisation des services nécessaires au contrôleur.
     * @param etudiantService Service pour la gestion des étudiants
     * @param noteService Service pour la gestion des notes
     * @param reclamationService Service pour la gestion des réclamations
     * @param sessionExamenService Service pour la gestion des sessions d'examen
     */
    public EtudiantController(EtudiantService etudiantService, NoteService noteService,
                            ReclamationService reclamationService, SessionExamenService sessionExamenService) {
        this.etudiantService = etudiantService;
        this.noteService = noteService;
        this.reclamationService = reclamationService;
        this.sessionExamenService = sessionExamenService;
    }

    // Affiche le relevé de notes de l'étudiant
    @GetMapping("/relevesNotes")
    public String showRelevesNotes(Model model, HttpSession session) {
        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId"); // Get ID from session

        // --- CRITICAL FIX: If no user ID in session, redirect to login ---
        if (loggedInEtudiantId == null) {
            return "redirect:/login?error=sessionExpired";
        }
        // -----------------------------------------------------------------

        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found with ID: " + loggedInEtudiantId));

        List<Note> notes = noteService.getNotesByEtudiant(etudiant);
        model.addAttribute("notes", notes);
        model.addAttribute("etudiant", etudiant);

        double overallAverage = notes.stream()
                .mapToDouble(note -> note.getValeur().doubleValue())
                .average()
                .orElse(0.0);
        model.addAttribute("overallAverage", overallAverage);

        return "relevésNotes";
    }

    // Affiche les réclamations de l'étudiant
    @GetMapping("/reclamations")
    public String showStudentReclamations(Model model, HttpSession session) {
        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInEtudiantId == null) {
            return "redirect:/login?error=sessionExpired";
        }

        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found for reclamations."));

        List<Reclamation> reclamations = reclamationService.getAllReclamations().stream()
                .filter(r -> r.getEtudiant() != null && r.getEtudiant().getId().equals(loggedInEtudiantId))
                .collect(Collectors.toList());

        model.addAttribute("reclamations", reclamations);
        model.addAttribute("etudiant", etudiant);

        return "reclamations";
    }

    // Affiche le planning des examens de l'étudiant
    @GetMapping("/planningExamens")
    public String showStudentExamPlanning(Model model, HttpSession session) {
        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId");

        if (loggedInEtudiantId == null) {
            return "redirect:/login?error=sessionExpired";
        }

        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found for exam planning."));

        List<Note> studentNotes = noteService.getNotesByEtudiant(etudiant);
        List<Long> examIds = studentNotes.stream()
                .filter(note -> note.getExamen() != null)
                .map(note -> note.getExamen().getId())
                .collect(Collectors.toList());

        List<SessionExamen> allSessions = sessionExamenService.getAllSessionExamens();
        List<SessionExamen> studentSessions = allSessions.stream()
                .filter(sessionExamen -> sessionExamen.getExamen() != null && examIds.contains(sessionExamen.getExamen().getId()))
                .collect(Collectors.toList());

        model.addAttribute("examSessions", studentSessions);
        model.addAttribute("etudiant", etudiant);

        return "planningExamens";
    }
}
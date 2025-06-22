//package com.example.sgeproject.controller;
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
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//@Controller
//@RequestMapping("/etudiant")
//public class EtudiantController {
//
//    private final EtudiantService etudiantService;
//    private final NoteService noteService;
//    private final ReclamationService reclamationService;
//    private final SessionExamenService sessionExamenService;
//
//    public EtudiantController(EtudiantService etudiantService, NoteService noteService,
//                              ReclamationService reclamationService, SessionExamenService sessionExamenService) {
//        this.etudiantService = etudiantService;
//        this.noteService = noteService;
//        this.reclamationService = reclamationService;
//        this.sessionExamenService = sessionExamenService;
//    }
//
//    /**
//     * Handles GET requests to display the student's grade report.
//     * Retrieves student ID from session.
//     *
//     * @param model The Model to pass data to the view.
//     * @param session The HttpSession to retrieve logged-in user ID.
//     * @return The name of the Thymeleaf template to render.
//     */
//    @GetMapping("/relevesNotes") // Handles GET /etudiant/relevesNotes
//    public String showRelevesNotes(Model model, HttpSession session) { // <<< CRITICAL: Add HttpSession parameter
//        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId"); // Get ID from session
//
//        if (loggedInEtudiantId == null) {
//            // Handle case where user is not "logged in" or session expired
//            throw new ResourceNotFoundException("No Etudiant ID found in session. Please log in.");
//        }
//
//        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found with ID: " + loggedInEtudiantId));
//
//        List<Note> notes = noteService.getNotesByEtudiant(etudiant);
//        model.addAttribute("notes", notes);
//        model.addAttribute("etudiant", etudiant);
//
//        double overallAverage = notes.stream()
//                .mapToDouble(note -> note.getValeur().doubleValue())
//                .average()
//                .orElse(0.0);
//        model.addAttribute("overallAverage", overallAverage);
//
//        return "relevésNotes";
//    }
//
//    /**
//     * Handles GET requests to display student's reclamations.
//     * Retrieves student ID from session.
//     *
//     * @param model The Model to pass data to the view.
//     * @param session The HttpSession to retrieve logged-in user ID.
//     * @return The name of the Thymeleaf template to render.
//     */
//    @GetMapping("/reclamations") // Handles GET /etudiant/reclamations
//    public String showStudentReclamations(Model model, HttpSession session) { // <<< CRITICAL: Add HttpSession parameter
//        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId"); // Get ID from session
//
//        if (loggedInEtudiantId == null) {
//            throw new ResourceNotFoundException("No Etudiant ID found in session. Please log in.");
//        }
//
//        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found for reclamations."));
//
//        List<Reclamation> reclamations = reclamationService.getAllReclamations().stream()
//                .filter(r -> r.getEtudiant() != null && r.getEtudiant().getId().equals(loggedInEtudiantId))
//                .collect(Collectors.toList());
//
//        model.addAttribute("reclamations", reclamations);
//        model.addAttribute("etudiant", etudiant);
//
//        return "reclamations";
//    }
//
//    /**
//     * Handles GET requests to display student's exam planning/schedule.
//     * Retrieves student ID from session.
//     *
//     * @param model The Model to pass data to the view.
//     * @param session The HttpSession to retrieve logged-in user ID.
//     * @return The name of the Thymeleaf template to render.
//     */
//    @GetMapping("/planningExamens") // Handles GET /etudiant/planningExamens
//    public String showStudentExamPlanning(Model model, HttpSession session) { // <<< CRITICAL: Add HttpSession parameter
//        Long loggedInEtudiantId = (Long) session.getAttribute("loggedInUserId"); // Get ID from session
//
//        if (loggedInEtudiantId == null) {
//            throw new ResourceNotFoundException("No Dietitian ID found in session. Please log in.");
//        }
//
//        Etudiant etudiant = etudiantService.getEtudiantById(loggedInEtudiantId)
//                .orElseThrow(() -> new ResourceNotFoundException("Logged-in Etudiant not found for exam planning."));
//
//        List<Note> studentNotes = noteService.getNotesByEtudiant(etudiant);
//        List<Long> examIds = studentNotes.stream()
//                .filter(note -> note.getExamen() != null)
//                .map(note -> note.getExamen().getId())
//                .collect(Collectors.toList());
//
//        List<SessionExamen> allSessions = sessionExamenService.getAllSessionExamens();
//        List<SessionExamen> studentSessions = allSessions.stream()
//                .filter(new Predicate<SessionExamen>() {
//                    @Override
//                    public boolean test(SessionExamen session) {
//                        return session.getExamen() != null && examIds.contains(session.getExamen().getId());
//                    }
//                })
//                .collect(Collectors.toList());
//
//        model.addAttribute("examSessions", studentSessions);
//        model.addAttribute("etudiant", etudiant);
//
//        return "planningExamens";
//    }
//}
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
import jakarta.servlet.http.HttpSession; // Ensure this is imported
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/etudiant")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final NoteService noteService;
    private final ReclamationService reclamationService;
    private final SessionExamenService sessionExamenService;

    public EtudiantController(EtudiantService etudiantService, NoteService noteService,
                              ReclamationService reclamationService, SessionExamenService sessionExamenService) {
        this.etudiantService = etudiantService;
        this.noteService = noteService;
        this.reclamationService = reclamationService;
        this.sessionExamenService = sessionExamenService;
    }

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
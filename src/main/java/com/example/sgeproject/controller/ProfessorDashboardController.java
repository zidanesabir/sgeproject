package com.example.sgeproject.controller;

import com.example.sgeproject.service.NoteService;
import com.example.sgeproject.service.ExamenService;
import com.example.sgeproject.service.ReclamationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professor")
public class ProfessorDashboardController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private ExamenService examenService;

    @Autowired
    private ReclamationService reclamationService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Simuler un professeur connecté (ID 1)
        Long professorId = 1L;

        int totalNotes = noteService.countByProfessorId(professorId);
        int totalExamens = examenService.countExamensForProfessor(professorId);
        int totalReclamations = reclamationService.countReclamationsForProfessor(professorId);

        model.addAttribute("totalNotes", totalNotes);
        model.addAttribute("totalExamens", totalExamens);
        model.addAttribute("totalReclamations", totalReclamations);

        return "professor/dashboard";
    }
}

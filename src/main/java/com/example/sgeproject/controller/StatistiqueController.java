package com.example.sgeproject.controller;

import com.example.sgeproject.service.StatistiqueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/admin/statistiques") // Base mapping for admin statistics operations
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    public StatistiqueController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    @GetMapping // Handles GET /admin/statistiques
    public String showStatistics(Model model) {
        // Fetch data from service and add to model
        model.addAttribute("totalStudents", statistiqueService.getTotalStudents());
        model.addAttribute("totalProfessors", statistiqueService.getTotalProfessors());
        model.addAttribute("totalExams", statistiqueService.getTotalExams());
        model.addAttribute("averageOverallNote", statistiqueService.getAverageOverallNote());

        Map<String, Long> gradeDistributionMap = statistiqueService.getGradeDistribution();
        model.addAttribute("gradeRanges", gradeDistributionMap.keySet());
        model.addAttribute("gradeCounts", gradeDistributionMap.values());

        Map<String, BigDecimal> modulePerformanceMap = statistiqueService.getModulePerformance();
        model.addAttribute("topModulesNames", modulePerformanceMap.keySet());
        model.addAttribute("topModulesAverages", modulePerformanceMap.values());

        return "statistiques"; // Renders src/main/resources/templates/statistiques.html
    }
}
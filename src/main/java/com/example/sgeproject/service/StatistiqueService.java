package com.example.sgeproject.service;

import com.example.sgeproject.model.Module;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    private final EtudiantRepository etudiantRepository;
    private final NoteRepository noteRepository;
    private final ModuleRepository moduleRepository;
    private final ProfesseurRepository professeurRepository;
    private final ExamenRepository examenRepository;

    public StatistiqueService(EtudiantRepository etudiantRepository,
                              NoteRepository noteRepository,
                              ModuleRepository moduleRepository,
                              ProfesseurRepository professeurRepository,
                              ExamenRepository examenRepository) {
        this.etudiantRepository = etudiantRepository;
        this.noteRepository = noteRepository;
        this.moduleRepository = moduleRepository;
        this.professeurRepository = professeurRepository;
        this.examenRepository = examenRepository;
    }

    public long getTotalStudents() {
        return etudiantRepository.count();
    }

    public long getTotalProfessors() {
        return professeurRepository.count();
    }

    public long getTotalExams() {
        return examenRepository.count();
    }

    public BigDecimal getAverageOverallNote() {
        // --- CRITICAL FIX: Use findAllWithAllDetails() ---
        List<Note> allNotes = noteRepository.findAllWithAllDetails();
        // --------------------------------------------------
        if (allNotes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        double sum = allNotes.stream().mapToDouble(note -> note.getValeur().doubleValue()).sum();
        return BigDecimal.valueOf(sum / allNotes.size()).setScale(2, RoundingMode.HALF_UP);
    }

    public Map<String, Long> getGradeDistribution() {
        // --- CRITICAL FIX: Use findAllWithAllDetails() ---
        List<Note> allNotes = noteRepository.findAllWithAllDetails();
        // --------------------------------------------------
        Map<String, Long> distribution = new HashMap<>();

        distribution.put("<10", allNotes.stream().filter(note -> note.getValeur().doubleValue() < 10).count());
        distribution.put("10-12", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 10 && note.getValeur().doubleValue() < 12).count());
        distribution.put("12-14", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 12 && note.getValeur().doubleValue() < 14).count());
        distribution.put("14-16", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 14 && note.getValeur().doubleValue() < 16).count());
        distribution.put("16-18", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 16 && note.getValeur().doubleValue() < 18).count());
        distribution.put("18-20", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 18).count());

        return distribution;
    }

    @Transactional // Add @Transactional if not already here, as it might be accessing lazy collections in future logic
    public Map<String, BigDecimal> getModulePerformance() {
        List<Module> allModules = moduleRepository.findAllWithDetails(); // Assuming ModuleRepository has this

        Map<String, BigDecimal> moduleAverages = new HashMap<>();

        for (Module module : allModules) {
            String moduleName = module.getNomModule();
            // --- CRITICAL FIX: Use findAllWithAllDetails() for notes and then filter ---
            List<Note> moduleNotes = noteRepository.findAllWithAllDetails().stream()
                    .filter(note -> note.getExamen() != null && note.getExamen().getModule() != null &&
                            note.getExamen().getModule().getId().equals(module.getId()))
                    .collect(Collectors.toList());
            // -------------------------------------------------------------------------

            if (!moduleNotes.isEmpty()) {
                double sum = moduleNotes.stream().mapToDouble(note -> note.getValeur().doubleValue()).sum();
                BigDecimal average = BigDecimal.valueOf(sum / moduleNotes.size()).setScale(2, RoundingMode.HALF_UP);
                moduleAverages.put(moduleName, average);
            } else {
                moduleAverages.put(moduleName, BigDecimal.ZERO);
            }
        }
        return moduleAverages.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));
    }
}
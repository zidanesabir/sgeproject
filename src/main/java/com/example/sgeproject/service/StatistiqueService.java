package com.example.sgeproject.service;

import com.example.sgeproject.model.Module;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatistiqueService {

    private final EtudiantRepository etudiantRepository;
    private final NoteRepository noteRepository;
    private final ModuleRepository moduleRepository;
    private final ProfesseurRepository professeurRepository; // CRITICAL: Inject ProfesseurRepository
    private final ExamenRepository examenRepository;       // CRITICAL: Inject ExamenRepository
    // private final PersonneRepository personneRepository; // If you want to filter by role from all persons

    public StatistiqueService(EtudiantRepository etudiantRepository,
                              NoteRepository noteRepository,
                              ModuleRepository moduleRepository,
                              ProfesseurRepository professeurRepository, // Inject
                              ExamenRepository examenRepository) {     // Inject
        this.etudiantRepository = etudiantRepository;
        this.noteRepository = noteRepository;
        this.moduleRepository = moduleRepository;
        this.professeurRepository = professeurRepository; // Assign
        this.examenRepository = examenRepository;         // Assign
    }

    public long getTotalStudents() {
        return etudiantRepository.count();
    }

    // --- CRITICAL FIX: Implement actual counts for Professors and Exams ---
    public long getTotalProfessors() {
        return professeurRepository.count(); // Use actual repository count
    }

    public long getTotalExams() {
        return examenRepository.count(); // Use actual repository count
    }
    // ---------------------------------------------------------------------

    public BigDecimal getAverageOverallNote() {
        // Use findAllWithDetails to ensure associated entities are loaded if needed for calculations
        List<Note> allNotes = noteRepository.findAllWithDetails(); // CRITICAL: Use findAllWithDetails
        if (allNotes.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        double sum = allNotes.stream().mapToDouble(note -> note.getValeur().doubleValue()).sum();
        return BigDecimal.valueOf(sum / allNotes.size()).setScale(2, RoundingMode.HALF_UP);
    }

    public Map<String, Long> getGradeDistribution() {
        List<Note> allNotes = noteRepository.findAllWithDetails(); // CRITICAL: Use findAllWithDetails
        Map<String, Long> distribution = new HashMap<>();

        // Ensure these counts handle cases where allNotes might be empty
        distribution.put("<10", allNotes.stream().filter(note -> note.getValeur().doubleValue() < 10).count());
        distribution.put("10-12", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 10 && note.getValeur().doubleValue() < 12).count());
        distribution.put("12-14", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 12 && note.getValeur().doubleValue() < 14).count());
        distribution.put("14-16", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 14 && note.getValeur().doubleValue() < 16).count());
        distribution.put("16-18", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 16 && note.getValeur().doubleValue() < 18).count());
        distribution.put("18-20", allNotes.stream().filter(note -> note.getValeur().doubleValue() >= 18).count());

        return distribution;
    }

    public Map<String, BigDecimal> getModulePerformance() {
        List<Module> allModules = moduleRepository.findAllWithDetails(); // CRITICAL: Use findAllWithDetails
        Map<String, BigDecimal> moduleAverages = new HashMap<>();

        if (allModules.isEmpty()) {
            return Collections.emptyMap(); // Return empty map if no modules
        }

        for (Module module : allModules) {
            String moduleName = module.getNomModule();
            // Filter notes for the current module.
            // Note: This collects ALL notes and then filters, which can be inefficient for large datasets.
            // A more optimized approach would be to add a custom query to NoteRepository:
            // `List<Note> findByExamen_Module(Module module);`
            List<Note> moduleNotes = noteRepository.findAllWithDetails().stream() // CRITICAL: Use findAllWithDetails for notes too
                    .filter(note -> note.getExamen() != null && note.getExamen().getModule() != null &&
                            note.getExamen().getModule().getId().equals(module.getId()))
                    .collect(Collectors.toList());

            if (!moduleNotes.isEmpty()) {
                double sum = moduleNotes.stream().mapToDouble(note -> note.getValeur().doubleValue()).sum();
                BigDecimal average = BigDecimal.valueOf(sum / moduleNotes.size()).setScale(2, RoundingMode.HALF_UP);
                moduleAverages.put(moduleName, average);
            } else {
                moduleAverages.put(moduleName, BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
        }
        return moduleAverages;
    }
}
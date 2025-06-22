package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.repository.ExamenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExamenService {

    private final ExamenRepository examenRepository;

    public ExamenService(ExamenRepository examenRepository) {
        this.examenRepository = examenRepository;
    }

    // --- CRITICAL FIX: Use the new findAllWithDetails() query ---
    public List<Examen> getAllExamens() {
        return examenRepository.findAllWithDetails(); // Use the custom query
    }
    // ----------------------------------------------------------

    public Optional<Examen> getExamenById(Long id) {
        return examenRepository.findById(id);
    }

    @Transactional
    public Examen saveExamen(Examen examen) {
        return examenRepository.save(examen);
    }

    @Transactional
    public Examen updateExamen(Long id, Examen examenDetails) {
        Examen existingExamen = examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + id));

        existingExamen.setDateExamen(examenDetails.getDateExamen());
        existingExamen.setDureeMinutes(examenDetails.getDureeMinutes());
        existingExamen.setTypeExamen(examenDetails.getTypeExamen());

        return examenRepository.save(existingExamen);
    }

    @Transactional
    public void deleteExamen(Long id) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + id));
        examenRepository.delete(examen);
    }
}
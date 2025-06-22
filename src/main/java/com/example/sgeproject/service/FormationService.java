package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException; // Needed for orElseThrow
import com.example.sgeproject.model.Formation;
import com.example.sgeproject.repository.FormationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // CRITICAL: Ensure this import is present

@Service
public class FormationService {

    private final FormationRepository formationRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    // --- CRITICAL FIX: This method was missing or incorrect ---
    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }
    // ------------------------------------------------------------

    @Transactional
    public Formation saveFormation(Formation formation) {
        return formationRepository.save(formation);
    }

    @Transactional
    public Formation updateFormation(Long id, Formation formationDetails) {
        Formation existingFormation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + id));

        existingFormation.setNomFormation(formationDetails.getNomFormation());
        existingFormation.setDescription(formationDetails.getDescription());
        // You might have other fields for Formation to update here

        return formationRepository.save(existingFormation);
    }

    @Transactional
    public void deleteFormation(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + id));
        formationRepository.delete(formation);
    }

    // You can add more specific query methods here if needed, e.g., findByNomFormation
}
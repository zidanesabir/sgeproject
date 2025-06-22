package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.SujetExamen;
import com.example.sgeproject.model.Examen; // Needed if linking Examen
import com.example.sgeproject.repository.SujetExamenRepository;
// import com.exampe.sgeproject.repository.ExamenRepository; // Unused if using ExamenService

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SujetExamenService {

    private final SujetExamenRepository sujetExamenRepository;
    private final ExamenService examenService; // Assuming you'll need this for linking

    public SujetExamenService(SujetExamenRepository sujetExamenRepository, ExamenService examenService) {
        this.sujetExamenRepository = sujetExamenRepository;
        this.examenService = examenService;
    }

    public List<SujetExamen> getAllSujetExamens() {
        return sujetExamenRepository.findAll();
    }

    public Optional<SujetExamen> getSujetExamenById(Long id) {
        return sujetExamenRepository.findById(id);
    }

    @Transactional
    public SujetExamen saveSujetExamen(SujetExamen sujetExamen) {
        // Ensure linked Examen object is loaded from DB if only ID is provided
        if (sujetExamen.getExamen() != null && sujetExamen.getExamen().getId() != null) {
            Examen examen = examenService.getExamenById(sujetExamen.getExamen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + sujetExamen.getExamen().getId()));
            sujetExamen.setExamen(examen);
        }
        return sujetExamenRepository.save(sujetExamen);
    }

    @Transactional
    public SujetExamen updateSujetExamen(Long id, SujetExamen sujetExamenDetails) {
        SujetExamen existingSujetExamen = sujetExamenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SujetExamen not found with id: " + id));

        existingSujetExamen.setTitre(sujetExamenDetails.getTitre());
        existingSujetExamen.setCheminFichier(sujetExamenDetails.getCheminFichier());

        // REMOVE THESE LINES, AS THESE FIELDS DO NOT EXIST IN YOUR SujetExamen MODEL:
        // existingSujetExamen.setContenu(sujetExamenDetails.getContenu());
        // existingSujetExamen.setCapacite(sujetExamenDetails.getCapacite());
        // existingSujetExamen.setStatut(sujetExamenDetails.getStatut());
        // existingSujetExamen.setDateValidation(sujetExamenDetails.getDateValidation());

        // Update linked Examen if changed
        if (sujetExamenDetails.getExamen() != null && sujetExamenDetails.getExamen().getId() != null &&
                (existingSujetExamen.getExamen() == null || !existingSujetExamen.getExamen().getId().equals(sujetExamenDetails.getExamen().getId()))) {
            Examen examen = examenService.getExamenById(sujetExamenDetails.getExamen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + sujetExamenDetails.getExamen().getId()));
            existingSujetExamen.setExamen(examen);
        }

        return sujetExamenRepository.save(existingSujetExamen);
    }

    @Transactional
    public void deleteSujetExamen(Long id) {
        SujetExamen sujetExamen = sujetExamenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SujetExamen not found with id: " + id));
        sujetExamenRepository.delete(sujetExamen);
    }
}
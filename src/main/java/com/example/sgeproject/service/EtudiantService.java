package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.repository.EtudiantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Ensure this import is present

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public List<Etudiant> getAllEtudiants() { // Corrected: Renamed from findAllEtudiants
        return etudiantRepository.findAll();
    }

    public Optional<Etudiant> getEtudiantById(Long id) { // Corrected: Renamed from findEtudiantById
        return etudiantRepository.findById(id);
    }

    @Transactional
    public Etudiant saveEtudiant(Etudiant etudiant) {
        return etudiantRepository.save(etudiant);
    }

    @Transactional
    public Etudiant updateEtudiant(Long id, Etudiant etudiantDetails) {
        Etudiant existingEtudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + id));

        existingEtudiant.setNom(etudiantDetails.getNom());
        existingEtudiant.setPrenom(etudiantDetails.getPrenom());
        existingEtudiant.setEmail(etudiantDetails.getEmail());
        // Add any Etudiant-specific fields if they exist in your model

        return etudiantRepository.save(existingEtudiant);
    }

    @Transactional
    public void deleteEtudiant(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + id));
        etudiantRepository.delete(etudiant);
    }
}
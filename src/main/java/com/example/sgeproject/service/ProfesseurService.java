package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Professeur;
import com.example.sgeproject.repository.ProfesseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesseurService {

    private final ProfesseurRepository professeurRepository;

    public ProfesseurService(ProfesseurRepository professeurRepository) {
        this.professeurRepository = professeurRepository;
    }

    public List<Professeur> getAllProfesseurs() {
        return professeurRepository.findAll();
    }

    public Optional<Professeur> getProfesseurById(Long id) {
        return professeurRepository.findById(id);
    }

    @Transactional
    public Professeur saveProfesseur(Professeur professeur) {
        // Add any specific business logic for saving/updating a Professeur
        return professeurRepository.save(professeur);
    }

    @Transactional
    public Professeur updateProfesseur(Long id, Professeur professeurDetails) {
        Professeur existingProfesseur = professeurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professeur not found with id: " + id));

        // Update common Personne fields if not handled by PersonneService
        existingProfesseur.setNom(professeurDetails.getNom());
        existingProfesseur.setPrenom(professeurDetails.getPrenom());
        existingProfesseur.setEmail(professeurDetails.getEmail()); // Corrected from getLogin()

        // Update any Professeur-specific fields

        return professeurRepository.save(existingProfesseur);
    }

    @Transactional
    public void deleteProfesseur(Long id) {
        Professeur professeur = professeurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professeur not found with id: " + id));
        professeurRepository.delete(professeur);
    }
}
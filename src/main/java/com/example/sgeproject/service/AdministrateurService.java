package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Administrateur;
import com.example.sgeproject.repository.AdministrateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdministrateurService {

    private final AdministrateurRepository administrateurRepository;

    public AdministrateurService(AdministrateurRepository administrateurRepository) {
        this.administrateurRepository = administrateurRepository;
    }

    public List<Administrateur> getAllAdministrateurs() {
        return administrateurRepository.findAll();
    }

    public Optional<Administrateur> getAdministrateurById(Long id) {
        return administrateurRepository.findById(id);
    }

    @Transactional
    public Administrateur saveAdministrateur(Administrateur administrateur) {
        // Add any specific business logic for saving/updating an Administrateur
        return administrateurRepository.save(administrateur);
    }

    @Transactional
    public Administrateur updateAdministrateur(Long id, Administrateur administrateurDetails) {
        Administrateur existingAdministrateur = administrateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur not found with id: " + id));

        // Update common Personne fields if not handled by PersonneService
        existingAdministrateur.setNom(administrateurDetails.getNom());
        existingAdministrateur.setPrenom(administrateurDetails.getPrenom());
        existingAdministrateur.setEmail(administrateurDetails.getEmail()); // Corrected from getLogin()

        // Update any Administrateur-specific fields

        return administrateurRepository.save(existingAdministrateur);
    }

    @Transactional
    public void deleteAdministrateur(Long id) {
        Administrateur administrateur = administrateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrateur not found with id: " + id));
        administrateurRepository.delete(administrateur);
    }

    // You might add specific methods like findByEmail here, but it's usually in PersonneRepository
    // public Optional<Administrateur> findByEmail(String email) {
    //     return administrateurRepository.findByEmail(email); // If AdministrateurRepository extends CustomBaseRepository
    // }
}
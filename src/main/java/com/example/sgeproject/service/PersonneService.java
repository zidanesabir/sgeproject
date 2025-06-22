package com.example.sgeproject.service;

import com.example.sgeproject.dto.RegistrationRequest;
import com.example.sgeproject.dto.UserCreationRequest;
import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Administrateur;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Professeur;
import com.example.sgeproject.model.Personne;
import com.example.sgeproject.repository.PersonneRepository;
// REMOVED: import org.springframework.security.crypto.password.PasswordEncoder; // No longer needed

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonneService {

    private final PersonneRepository personneRepository;
    // REMOVED: private final PasswordEncoder passwordEncoder; // No longer injected

    // Adjusted constructor
    public PersonneService(PersonneRepository personneRepository) {
        this.personneRepository = personneRepository;
        // REMOVED: this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Etudiant registerEtudiant(RegistrationRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if (personneRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        Etudiant etudiant = new Etudiant();
        etudiant.setNom(request.getNom());
        etudiant.setPrenom(request.getPrenom());
        etudiant.setEmail(request.getEmail());
        etudiant.setPassword(request.getPassword()); // CRITICAL: Storing plain text password. HIGHLY INSECURE.
        etudiant.setRole("ETUDIANT");

        return personneRepository.save(etudiant);
    }

    @Transactional
    public Personne createUser(UserCreationRequest request) {
        if (request.getPassword() != null && !request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if (personneRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        Personne newUser;
        switch (request.getRole().toUpperCase()) {
            case "ETUDIANT":
                newUser = new Etudiant();
                break;
            case "PROFESSOR":
                newUser = new Professeur();
                break;
            case "ADMIN":
                newUser = new Administrateur();
                break;
            default:
                throw new IllegalArgumentException("Invalid user role specified: " + request.getRole());
        }

        newUser.setNom(request.getNom());
        newUser.setPrenom(request.getPrenom());
        newUser.setEmail(request.getEmail());
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required for new user creation.");
        }
        newUser.setPassword(request.getPassword()); // CRITICAL: Storing plain text password. HIGHLY INSECURE.
        newUser.setRole(request.getRole().toUpperCase());

        return personneRepository.save(newUser);
    }

    @Transactional
    public Personne updateUser(Long id, UserCreationRequest request) {
        Personne existingUser = personneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personne not found with id: " + id));

        if (!existingUser.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (personneRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered by another user.");
            }
        }

        existingUser.setNom(request.getNom());
        existingUser.setPrenom(request.getPrenom());
        existingUser.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(request.getPassword()); // CRITICAL: Storing plain text password. HIGHLY INSECURE.
        }

        existingUser.setRole(request.getRole().toUpperCase());

        return personneRepository.save(existingUser);
    }

    public List<Personne> getAllPersonnes() {
        return personneRepository.findAll();
    }

    public Optional<Personne> getPersonneById(Long id) {
        return personneRepository.findById(id);
    }

    @Transactional
    public void deletePersonne(Long id) {
        Personne personne = personneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Personne not found with id: " + id));
        personneRepository.delete(personne);
    }

    public Optional<Personne> findByEmail(String email) {
        return personneRepository.findByEmail(email);
    }

    // --- New method for basic "login" without Spring Security ---
    // This is a manual check, not real authentication.
    public Optional<Personne> authenticateUser(String email, String password) {
        Optional<Personne> userOptional = personneRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            Personne user = userOptional.get();
            // WARNING: Comparing plain-text passwords directly is HIGHLY INSECURE.
            // This is for demonstration purposes ONLY without Spring Security.
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
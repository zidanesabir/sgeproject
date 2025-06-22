package com.example.sgeproject.repository;

import com.example.sgeproject.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // Needed for Optional return type

@Repository
public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {
    // --- CRITICAL FIX: Change findByLogin to findByEmail ---
    // Your Administrateur model (inherited from Personne) has an 'email' field, not a 'login' field.
    Optional<Administrateur> findByEmail(String email);
    // --------------------------------------------------------

    // Any other specific query methods for Administrateur would go here.
}
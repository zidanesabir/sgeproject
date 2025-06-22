package com.example.sgeproject.repository;

import com.example.sgeproject.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    // --- CRITICAL FIX: Change findByLogin to findByEmail ---
    // Your Etudiant model (inherited from Personne) has an 'email' field, not a 'login' field.
    Optional<Etudiant> findByEmail(String email);
    // --------------------------------------------------------

    // If you had other specific queries for Etudiant, they would go here.
    // Example: List<Etudiant> findByFormation(Formation formation);
}
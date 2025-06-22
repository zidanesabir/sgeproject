package com.example.sgeproject.repository;

import com.example.sgeproject.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesseurRepository extends JpaRepository<Professeur, Long> {
    // --- CRITICAL FIX: REMOVE THE FOLLOWING LINE (if it exists) ---
    // List<Professeur> findBySpecialite(String specialite);
    // ----------------------------------------------------------------

    // Your Professeur model (inherited from Personne) does NOT have a 'specialite' field by default.
    // If you need to find by email, that's handled by PersonneRepository.findByEmail.
}
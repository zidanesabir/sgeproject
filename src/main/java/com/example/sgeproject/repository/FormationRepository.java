package com.example.sgeproject.repository;

import com.example.sgeproject.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    // --- CRITICAL FIX: Change findByIntitule to findByNomFormation ---
    // Your Formation model has 'nomFormation', not 'intitule'.
    Optional<Formation> findByNomFormation(String nomFormation);
    // ------------------------------------------------------------------

    // Any other specific query methods for Formation would go here.
}
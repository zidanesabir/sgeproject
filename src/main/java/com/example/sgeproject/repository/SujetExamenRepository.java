package com.example.sgeproject.repository;

import com.example.sgeproject.model.SujetExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SujetExamenRepository extends JpaRepository<SujetExamen, Long> {
    Optional<SujetExamen> findByTitre(String titre); // This is likely fine as 'titre' exists

    // --- CRITICAL FIX: REMOVE THE FOLLOWING LINE (if it exists) ---
    // List<SujetExamen> findByStatut(String statut);
    // ---------------------------------------------------------------
}
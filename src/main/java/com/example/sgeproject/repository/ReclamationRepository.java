package com.example.sgeproject.repository;

import com.example.sgeproject.model.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    // --- CRITICAL FIX: Change findByDateCreationAfter to findByDateReclamationAfter ---
    // Your Reclamation model has 'dateReclamation' (LocalDate), not 'dateCreation'.
    List<Reclamation> findByDateReclamationAfter(LocalDate date);
    // ----------------------------------------------------------------------------------

    // Any other specific query methods for Reclamation would go here.
}
package com.example.sgeproject.repository;

import com.example.sgeproject.model.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByDateReclamationAfter(LocalDate date);

    @Query("SELECT r FROM Reclamation r JOIN FETCH r.etudiant JOIN FETCH r.note")
    List<Reclamation> findAllWithDetails();

    // --- CRITICAL FIX: Add a specific JOIN FETCH query for finding by ID ---
    // This ensures etudiant and note are loaded when retrieving a single reclamation.
    @Query("SELECT r FROM Reclamation r JOIN FETCH r.etudiant JOIN FETCH r.note WHERE r.id = :id")
    Optional<Reclamation> findByIdWithDetails(@Param("id") Long id);
    // -------------------------------------------------------------------------
}
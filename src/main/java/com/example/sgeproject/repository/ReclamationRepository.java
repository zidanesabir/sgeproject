package com.example.sgeproject.repository;

import com.example.sgeproject.model.Reclamation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    List<Reclamation> findByDateReclamationAfter(LocalDate date);

    // --- CRITICAL FIX: Add a custom query to eagerly fetch etudiant and note ---
    @Query("SELECT r FROM Reclamation r JOIN FETCH r.etudiant JOIN FETCH r.note")
    List<Reclamation> findAllWithDetails();
    // -------------------------------------------------------------------------
}
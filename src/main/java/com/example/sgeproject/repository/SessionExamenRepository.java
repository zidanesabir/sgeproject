package com.example.sgeproject.repository;

import com.example.sgeproject.model.SessionExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionExamenRepository extends JpaRepository<SessionExamen, Long> {
    // --- CRITICAL FIX: Aggressive JOIN FETCH to load all needed nested associations ---
    @Query("SELECT se FROM SessionExamen se " +
            "JOIN FETCH se.examen e " +          // Fetch the examen
            "JOIN FETCH e.module m " +           // Fetch the module of the examen
            "JOIN FETCH m.formation " +          // Fetch the formation of the module
            "JOIN FETCH m.professeur " +         // Fetch the professeur of the module
            "JOIN FETCH se.salle")               // Fetch the salle of the session
    List<SessionExamen> findAllWithAllDetailsForDisplay(); // New method name to reflect its comprehensiveness
    // ----------------------------------------------------------------------------------

    Optional<SessionExamen> findById(Long id); // Keep the basic findById
}
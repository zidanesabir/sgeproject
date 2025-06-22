package com.example.sgeproject.repository;

import com.example.sgeproject.model.SessionExamen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionExamenRepository extends JpaRepository<SessionExamen, Long> {

    // --- CRITICAL FIX: Add JOIN FETCH for superviseurs ---
    @Query("SELECT se FROM SessionExamen se " +
            "JOIN FETCH se.examen e " +
            "JOIN FETCH e.module m " +
            "JOIN FETCH m.formation " +
            "JOIN FETCH m.professeur " +
            "JOIN FETCH se.salle " +
            "LEFT JOIN FETCH se.superviseurs s") // Use LEFT JOIN FETCH for ManyToMany collections
    // in case a session has no supervisors, it won't filter out the session.
    List<SessionExamen> findAllWithAllDetailsForDisplay();
    // ----------------------------------------------------------------------------------

    Optional<SessionExamen> findById(Long id);
}
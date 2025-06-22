package com.example.sgeproject.repository;

import com.example.sgeproject.model.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {
    // --- CRITICAL FIX: Add a custom query to eagerly fetch Module ---
    @Query("SELECT e FROM Examen e JOIN FETCH e.module")
    List<Examen> findAllWithDetails();
    // ----------------------------------------------------------------
}
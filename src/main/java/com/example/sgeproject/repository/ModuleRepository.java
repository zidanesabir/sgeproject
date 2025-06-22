package com.example.sgeproject.repository;

import com.example.sgeproject.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    Optional<Module> findByNomModule(String nomModule);

    // --- CRITICAL FIX: Add a custom query to eagerly fetch Formation and Professeur ---
    @Query("SELECT m FROM Module m JOIN FETCH m.formation JOIN FETCH m.professeur")
    List<Module> findAllWithDetails();
    // ----------------------------------------------------------------------------------
}
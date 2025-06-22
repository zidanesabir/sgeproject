package com.example.sgeproject.repository;

import com.example.sgeproject.model.Salles; // CONFIRMED: Import Salles (plural)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SallesRepository extends JpaRepository<Salles, Long> { // CONFIRMED: Reference Salles (plural)
    // No custom methods needed here initially, basic CRUD is provided by JpaRepository
}
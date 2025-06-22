package com.example.sgeproject.repository;

import com.example.sgeproject.model.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Long> {
    // This is the method that PersonneService tries to call
    Optional<Personne> findByEmail(String email);
}
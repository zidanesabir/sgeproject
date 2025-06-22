package com.example.sgeproject.repository;

import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    // You likely have List<Note> findByEtudiant(Etudiant etudiant);

    // --- CRITICAL FIX: Add a custom query to eagerly fetch examen and its module ---
    @Query("SELECT n FROM Note n JOIN FETCH n.examen e JOIN FETCH e.module WHERE n.etudiant = :etudiant")
    List<Note> findByEtudiantWithExamAndModule(Etudiant etudiant);

    // Also consider a findAll with details if other places call getAllNotes() and need these:
    @Query("SELECT n FROM Note n JOIN FETCH n.examen e JOIN FETCH e.module JOIN FETCH n.etudiant")
    List<Note> findAllWithAllDetails();
    // -------------------------------------------------------------------------------
}
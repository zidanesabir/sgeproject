package com.example.sgeproject.repository;

import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // --- CRITICAL FIX: Add this method to eagerly fetch associated entities ---
    @Query("SELECT n FROM Note n JOIN FETCH n.etudiant JOIN FETCH n.examen")
    List<Note> findAllWithDetails();
    // -------------------------------------------------------------------------

    List<Note> findByEtudiant(Etudiant etudiant);

    List<Note> findByExamen(Examen examen);

    Optional<Note> findByEtudiantAndExamen(Etudiant etudiant, Examen examen);

    List<Note> findByValeurGreaterThan(BigDecimal value); // Changed from Double to BigDecimal for consistency

    // Note: 'estValidee' and 'commentaire' fields are not in your Note.java model based on my last shared code for Note.java.
    // If you add them, you'll need to create the corresponding columns in the 'note' table.
    // If these methods are causing compilation errors, comment them out until 'estValidee' is added to Note.java.
    // List<Note> findByEstValideeTrue();
    // List<Note> findByEstValideeFalse();
}
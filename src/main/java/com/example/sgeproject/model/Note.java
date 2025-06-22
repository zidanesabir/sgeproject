package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Lombok generates this
@AllArgsConstructor // Lombok generates this
@Entity
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valeur;

    @Column(name = "date_saisie", nullable = false)
    private LocalDate dateSaisie;

    private String commentaire;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "est_validee")
    private boolean estValidee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant; // This should be set by the controller/service

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examen_id")
    private Examen examen; // This should be set by the controller/service

    // --- CRITICAL FIX (Alternative for @NoArgsConstructor problems): ---
    // If you remove @NoArgsConstructor or @AllArgsConstructor, you need to manually handle constructors.
    // If @NoArgsConstructor is present, and you're doing `new Note()`, then 'etudiant' and 'examen' will be null.
    // Th:field can often handle nulls with safe navigation (e.g., `*{etudiant?.id}`).
    // The previous error implies `note.etudiant` itself is null when `th:field="*{etudiant.id}"` is evaluated.
    // The solution is to either:
    // A) Make sure `model.addAttribute("note", new Note())` also initializes `note.setEtudiant(new Etudiant()); note.setExamen(new Examen());`
    // B) Change `th:field="*{etudiant.id}"` to `th:field="*{etudiant?.id}"` (using safe navigation in HTML) for the IDs.
    // C) Create a custom constructor in Note that initializes these ManyToOne relations to new objects (less common).
    // Let's go with B) in HTML to be safe, combined with ensuring the controller sets an empty note.

    // No need to change this Note model file directly related to the error,
    // as it's the *controller's* responsibility to provide a valid 'note' object.
}
package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data; // <-- This is crucial for getters/setters
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data // This annotation generates getters and setters for dateReclamation and raison
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reclamation")
public class Reclamation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_reclamation", nullable = false)
    private LocalDate dateReclamation; // Field for setDateReclamation()

    @Column(nullable = false, columnDefinition = "TEXT")
    private String raison; // Field for getRaison()

    @Column(nullable = false)
    private String statut; // e.g., "Pending", "Approved", "Rejected"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", unique = true) // One-to-one with Note
    private Note note;
}
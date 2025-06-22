package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;
import java.util.HashSet; // For HashSet initialization
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session_examen")
public class SessionExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id")
    private Examen examen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Salles salle;

    // --- CRITICAL FIX: Ensure initialization and FetchType.LAZY (JOIN FETCH handles loading) ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // LAZY is fine with JOIN FETCH
    @JoinTable(
            name = "session_examen_superviseurs",
            joinColumns = @JoinColumn(name = "session_examen_id"),
            inverseJoinColumns = @JoinColumn(name = "professeur_id")
    )
    private Set<Professeur> superviseurs = new HashSet<>(); // Initialize set to prevent null
}
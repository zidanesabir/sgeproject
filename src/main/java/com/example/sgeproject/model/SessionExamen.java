package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;
import java.util.Set;
import java.util.HashSet; // Import for HashSet initialization if needed

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

    @ManyToOne(fetch = FetchType.LAZY) // Can remain LAZY, as we're using JOIN FETCH in repository
    @JoinColumn(name = "examen_id")
    private Examen examen;

    @ManyToOne(fetch = FetchType.LAZY) // Can remain LAZY, as we're using JOIN FETCH in repository
    @JoinColumn(name = "salle_id")
    private Salles salle; // Changed from Salle to Salles

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "session_examen_superviseurs",
            joinColumns = @JoinColumn(name = "session_examen_id"),
            inverseJoinColumns = @JoinColumn(name = "professeur_id")
    )
    private Set<Professeur> superviseurs = new HashSet<>(); // Initialize set to prevent null
}
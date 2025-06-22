package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data // This automatically generates getters and setters
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "salle") // Table name can remain singular or be plural if you prefer 'salles'
public class Salles { // CONFIRMED: Class name is Salles (plural)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_salle", unique = true, nullable = false)
    private String nomSalle;

    @Column(nullable = false)
    private Integer capacite;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionExamen> sessionsExamen; // A Salles can host many SessionExamens

    // No other custom methods should be here unless specifically added and error-checked.
}
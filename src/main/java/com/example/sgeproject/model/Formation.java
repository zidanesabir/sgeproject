package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data; // <--- CRITICAL: This generates setNomFormation and setDescription
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data // Ensure this annotation is present and correctly imported
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "formation")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_formation", unique = true, nullable = false)
    private String nomFormation; // This field should generate getNomFormation/setNomFormation

    private String description; // This field should generate getDescription/setDescription

    // Optional: One-to-Many relationship with Etudiant (if Etudiant has a formation)
    // @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Etudiant> etudiants;

    // Optional: One-to-Many relationship with Module
    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules;
}
package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personne")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator_type",
        discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("PERSONNE") // Base value, typically only used if you have instances of abstract Personne (rare)
public abstract class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;

    private String password; // Storing plain text without security now. HIGHLY INSECURE.

    private String role; // e.g., "ETUDIANT", "PROFESSOR", "ADMIN" - for your custom logic

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    private String adresse;
    private String telephone;

    // Added a field for 'fonction' as it appeared in one of your Hibernate DDLs
    // If you don't intend to use this, you can remove it and the corresponding column from DB
    private String fonction; // For Admin, Professor specific roles

    // Added a field for 'formation_id' in Personne, which exists in your DDL
    // This needs to be consistent. If Etudiant also has Formation, this should be handled carefully.
    // Given 'formation_id' appeared at the Personne level in your DDL, we'll place it here
    // but its actual value would only be meaningful for Etudiant types.
    @Column(name = "formation_id")
    private Long formationId; // Store formation ID directly if not using @ManyToOne on Etudiant yet

    public Personne(String nom, String prenom, String email, String password, String role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
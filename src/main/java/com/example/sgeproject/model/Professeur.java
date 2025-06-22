package com.example.sgeproject.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@DiscriminatorValue("PROFESSOR") // <<< CRITICAL: This value MUST match the DB 'PROFESSOR'
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Professeur extends Personne {

    // If you need a collection of Modules taught by this Professor:
    @OneToMany(mappedBy = "professeur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modulesEnseignes;

    public Professeur(String nom, String prenom, String email, String password) {
        super(nom, prenom, email, password, "PROFESSOR"); // Ensure role is PROFESSOR
    }
}
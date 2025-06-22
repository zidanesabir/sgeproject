package com.example.sgeproject.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("ADMIN") // <<< CRITICAL: This value MUST match the DB 'ADMIN'
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Administrateur extends Personne {

    public Administrateur(String nom, String prenom, String email, String password) {
        super(nom, prenom, email, password, "ADMIN"); // Ensure role is ADMIN
    }
}
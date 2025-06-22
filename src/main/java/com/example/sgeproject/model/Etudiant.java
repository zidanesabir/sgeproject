package com.example.sgeproject.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@DiscriminatorValue("ETUDIANT") // <<< CRITICAL: This value MUST match the DB 'ETUDIANT'
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Etudiant extends Personne {

    // The 'formation_id' column is now in Personne, but you can still have a
    // @ManyToOne 'formation' object if you want proper object relationships.
    // If you uncommented this previously, ensure it's still correct.
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "formation_id", insertable = false, updatable = false) // Link to column in base table
    // private Formation formation;

    public Etudiant(String nom, String prenom, String email, String password) {
        super(nom, prenom, email, password, "ETUDIANT"); // Ensure role is ETUDIANT
    }
}
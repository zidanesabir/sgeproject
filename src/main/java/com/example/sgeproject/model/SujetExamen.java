package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// import java.time.LocalDate; // You'd need this if adding dateValidation

@Data // This correctly generates getters/setters for the declared fields
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sujet_examen")
public class SujetExamen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(name = "chemin_fichier")
    private String cheminFichier;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examen_id", unique = true) // This is the foreign key column
    private Examen examen; // Field name is 'examen' (lowercase)

    // The fields 'contenu', 'capacite', 'statut', 'dateValidation' are NOT present in this model.
    // If you intend to use them, you must uncomment/add them here:
    /*
    @Column(columnDefinition = "TEXT")
    private String contenu; // Example: actual content of the exam subject

    private Integer capacite; // This field seems misplaced in SujetExamen, it's usually for Salle.
                             // Consider if it belongs here or in another entity.

    private String statut; // Example: "Draft", "Final", "Approved"
    private LocalDate dateValidation;
    */
}
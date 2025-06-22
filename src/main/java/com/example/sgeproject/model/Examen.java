package com.example.sgeproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "examen")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_examen", nullable = false)
    private LocalDate dateExamen;

    @Column(name = "duree_minutes", nullable = false)
    private Integer dureeMinutes;

    @Column(name = "type_examen", nullable = false)
    private String typeExamen;

    @ManyToOne(fetch = FetchType.EAGER) // <<< CRITICAL FIX: Changed to EAGER
    @JoinColumn(name = "module_id")
    private Module module;

    @OneToOne(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SujetExamen sujetExamen;

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessionExamen> sessionsExamen;

    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;
}
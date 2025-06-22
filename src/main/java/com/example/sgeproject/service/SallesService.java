package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Salles; // CONFIRMED: Import Salles (plural)
import com.example.sgeproject.repository.SallesRepository; // CONFIRMED: Reference SallesRepository (plural)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SallesService { // CONFIRMED: Class name is SallesService (plural)

    private final SallesRepository sallesRepository; // CONFIRMED: Reference SallesRepository (plural)

    public SallesService(SallesRepository sallesRepository) { // Constructor uses SallesRepository
        this.sallesRepository = sallesRepository;
    }

    public List<Salles> getAllSalles() { // Method returning List of Salles
        return sallesRepository.findAll();
    }

    public Optional<Salles> getSallesById(Long id) { // Method returning Optional<Salles> (renamed from getSalleById for consistency)
        return sallesRepository.findById(id);
    }

    @Transactional
    public Salles saveSalles(Salles salles) { // Method accepting Salles parameter (renamed from saveSalle for consistency)
        return sallesRepository.save(salles);
    }

    @Transactional
    public Salles updateSalles(Long id, Salles sallesDetails) { // Method accepting Salles parameter (renamed from updateSalle for consistency)
        Salles existingSalles = sallesRepository.findById(id) // Finding Salles by ID
                .orElseThrow(() -> new ResourceNotFoundException("Salles not found with id: " + id));

        existingSalles.setNomSalle(sallesDetails.getNomSalle());
        existingSalles.setCapacite(sallesDetails.getCapacite());

        return sallesRepository.save(existingSalles);
    }

    @Transactional
    public void deleteSalles(Long id) { // Method for deleting Salles (renamed from deleteSalle for consistency)
        Salles salles = sallesRepository.findById(id) // Finding Salles by ID
                .orElseThrow(() -> new ResourceNotFoundException("Salles not found with id: " + id));
        sallesRepository.delete(salles);
    }
}
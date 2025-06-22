package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Formation;
import com.example.sgeproject.model.Module;
import com.example.sgeproject.model.Professeur;
import com.example.sgeproject.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final FormationService formationService;
    private final ProfesseurService professeurService;

    public ModuleService(ModuleRepository moduleRepository,
                         FormationService formationService,
                         ProfesseurService professeurService) {
        this.moduleRepository = moduleRepository;
        this.formationService = formationService;
        this.professeurService = professeurService;
    }

    // --- CRITICAL FIX: Use the new findAllWithDetails() query ---
    public List<Module> getAllModules() {
        return moduleRepository.findAllWithDetails(); // Use the custom query
    }
    // -----------------------------------------------------------

    public Optional<Module> getModuleById(Long id) {
        return moduleRepository.findById(id);
    }

    @Transactional
    public Module saveModule(Module module) {
        if (module.getFormation() != null && module.getFormation().getId() != null) {
            Formation formation = formationService.getFormationById(module.getFormation().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + module.getFormation().getId()));
            module.setFormation(formation);
        }
        if (module.getProfesseur() != null && module.getProfesseur().getId() != null) {
            Professeur professeur = professeurService.getProfesseurById(module.getProfesseur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professeur not found with id: " + module.getProfesseur().getId()));
            module.setProfesseur(professeur);
        }
        return moduleRepository.save(module);
    }

    @Transactional
    public Module updateModule(Long id, Module moduleDetails) {
        Module existingModule = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + id));

        existingModule.setNomModule(moduleDetails.getNomModule());
        existingModule.setDescription(moduleDetails.getDescription());
        existingModule.setCoefficient(moduleDetails.getCoefficient());

        if (moduleDetails.getFormation() != null && moduleDetails.getFormation().getId() != null &&
                (existingModule.getFormation() == null || !existingModule.getFormation().getId().equals(moduleDetails.getFormation().getId()))) {
            Formation formation = formationService.getFormationById(moduleDetails.getFormation().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Formation not found with id: " + moduleDetails.getFormation().getId()));
            existingModule.setFormation(formation);
        }
        if (moduleDetails.getProfesseur() != null && moduleDetails.getProfesseur().getId() != null &&
                (existingModule.getProfesseur() == null || !existingModule.getProfesseur().getId().equals(moduleDetails.getProfesseur().getId()))) {
            Professeur professeur = professeurService.getProfesseurById(moduleDetails.getProfesseur().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Professeur not found with id: " + moduleDetails.getProfesseur().getId()));
            existingModule.setProfesseur(professeur);
        } else if (moduleDetails.getProfesseur() == null) {
            existingModule.setProfesseur(null);
        }

        return moduleRepository.save(existingModule);
    }

    @Transactional
    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + id));
        moduleRepository.delete(module);
    }
}
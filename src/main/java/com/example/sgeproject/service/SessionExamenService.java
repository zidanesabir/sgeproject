package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.SessionExamen;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.model.Salles; // Changed from Salle to Salles, plural
import com.example.sgeproject.repository.SessionExamenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SessionExamenService {

    private final SessionExamenRepository sessionExamenRepository;
    private final ExamenService examenService;
    private final SallesService sallesService; // Changed from SalleService to SallesService

    public SessionExamenService(SessionExamenRepository sessionExamenRepository,
                                ExamenService examenService,
                                SallesService sallesService) {
        this.sessionExamenRepository = sessionExamenRepository;
        this.examenService = examenService;
        this.sallesService = sallesService;
    }

    public List<SessionExamen> getAllSessionExamens() {
        return sessionExamenRepository.findAllWithAllDetailsForDisplay(); // Use the comprehensive JOIN FETCH query
    }

    public Optional<SessionExamen> getSessionExamenById(Long id) {
        return sessionExamenRepository.findById(id);
    }

    // --- CRITICAL: Ensure these methods are present and match signatures ---
    @Transactional
    public SessionExamen saveSessionExamen(SessionExamen sessionExamen) {
        if (sessionExamen.getExamen() != null && sessionExamen.getExamen().getId() != null) {
            Examen examen = examenService.getExamenById(sessionExamen.getExamen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + sessionExamen.getExamen().getId()));
            sessionExamen.setExamen(examen);
        }
        if (sessionExamen.getSalle() != null && sessionExamen.getSalle().getId() != null) {
            Salles salles = sallesService.getSallesById(sessionExamen.getSalle().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Salles not found with id: " + sessionExamen.getSalle().getId()));
            sessionExamen.setSalle(salles);
        }
        return sessionExamenRepository.save(sessionExamen);
    }

    @Transactional
    public SessionExamen updateSessionExamen(Long id, SessionExamen sessionExamenDetails) {
        SessionExamen existingSessionExamen = sessionExamenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SessionExamen not found with id: " + id));

        existingSessionExamen.setHeureDebut(sessionExamenDetails.getHeureDebut());
        existingSessionExamen.setHeureFin(sessionExamenDetails.getHeureFin());

        if (sessionExamenDetails.getExamen() != null && sessionExamenDetails.getExamen().getId() != null &&
                (existingSessionExamen.getExamen() == null || !existingSessionExamen.getExamen().getId().equals(sessionExamenDetails.getExamen().getId()))) {
            Examen examen = examenService.getExamenById(sessionExamenDetails.getExamen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + sessionExamenDetails.getExamen().getId()));
            existingSessionExamen.setExamen(examen);
        }
        if (sessionExamenDetails.getSalle() != null && sessionExamenDetails.getSalle().getId() != null &&
                (existingSessionExamen.getSalle() == null || !existingSessionExamen.getSalle().getId().equals(sessionExamenDetails.getSalle().getId()))) {
            Salles salles = sallesService.getSallesById(sessionExamenDetails.getSalle().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Salles not found with id: " + sessionExamenDetails.getSalle().getId()));
            existingSessionExamen.setSalle(salles);
        }
        return sessionExamenRepository.save(existingSessionExamen);
    }

    @Transactional
    public void deleteSessionExamen(Long id) {
        SessionExamen sessionExamen = sessionExamenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SessionExamen not found with id: " + id));
        sessionExamenRepository.delete(sessionExamen);
    }
    // ----------------------------------------------------------------------
}
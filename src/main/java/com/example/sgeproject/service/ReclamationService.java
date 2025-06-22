package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.model.Reclamation;
import com.example.sgeproject.repository.ReclamationRepository;
import com.example.sgeproject.repository.EtudiantRepository;
import com.example.sgeproject.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final EtudiantRepository etudiantRepository;
    private final NoteRepository noteRepository;

    public ReclamationService(ReclamationRepository reclamationRepository,
                              EtudiantRepository etudiantRepository,
                              NoteRepository noteRepository) {
        this.reclamationRepository = reclamationRepository;
        this.etudiantRepository = etudiantRepository;
        this.noteRepository = noteRepository;
    }

    public List<Reclamation> getAllReclamations() { // Corrected: Renamed from findAllReclamations
        return reclamationRepository.findAll();
    }

public int countReclamationsForProfessor(Long professorId) {
    return reclamationRepository.countByProfessorId(professorId);
}

    public Optional<Reclamation> getReclamationById(Long id) { // Corrected: Renamed from findReclamationById
        return reclamationRepository.findById(id);
    }

    @Transactional
    public Reclamation saveReclamation(Reclamation reclamation) {
        if (reclamation.getEtudiant() != null && reclamation.getEtudiant().getId() != null) {
            Etudiant etudiant = etudiantRepository.findById(reclamation.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + reclamation.getEtudiant().getId()));
            reclamation.setEtudiant(etudiant);
        }
        if (reclamation.getNote() != null && reclamation.getNote().getId() != null) {
            Note note = noteRepository.findById(reclamation.getNote().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + reclamation.getNote().getId()));
            reclamation.setNote(note);
        }

        reclamation.setStatut("Pending");
        reclamation.setDateReclamation(LocalDate.now());

        return reclamationRepository.save(reclamation);
    }

    @Transactional
    public Reclamation updateReclamation(Long id, Reclamation reclamationDetails) {
        Reclamation existingReclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));

        existingReclamation.setRaison(reclamationDetails.getRaison());
        existingReclamation.setStatut(reclamationDetails.getStatut());

        if (reclamationDetails.getEtudiant() != null && reclamationDetails.getEtudiant().getId() != null &&
                (existingReclamation.getEtudiant() == null || !existingReclamation.getEtudiant().getId().equals(reclamationDetails.getEtudiant().getId()))) {
            Etudiant etudiant = etudiantRepository.findById(reclamationDetails.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + reclamationDetails.getEtudiant().getId()));
            existingReclamation.setEtudiant(etudiant);
        }
        if (reclamationDetails.getNote() != null && reclamationDetails.getNote().getId() != null &&
                (existingReclamation.getNote() == null || !existingReclamation.getNote().getId().equals(reclamationDetails.getNote().getId()))) {
            Note note = noteRepository.findById(reclamationDetails.getNote().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + reclamationDetails.getNote().getId()));
            existingReclamation.setNote(note);
        }

        return reclamationRepository.save(existingReclamation);
    }

    @Transactional
    public void deleteReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        reclamationRepository.delete(reclamation);
    }

    @Transactional
    public Reclamation approveReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        reclamation.setStatut("Approved");
        return reclamationRepository.save(reclamation);
    }

    @Transactional
    public Reclamation rejectReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        reclamation.setStatut("Rejected");
        return reclamationRepository.save(reclamation);
    }
}
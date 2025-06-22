package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.model.Reclamation;
import com.example.sgeproject.repository.ReclamationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;
    private final EtudiantService etudiantService;
    private final NoteService noteService;

    public ReclamationService(ReclamationRepository reclamationRepository, ReclamationRepository reclamationRepository1, EtudiantService etudiantService, NoteService noteService) {
        ReclamationRepository repository1;
        repository1 = reclamationRepository1;
        repository1 = reclamationRepository;
        this.reclamationRepository = repository1;
        this.etudiantService = etudiantService;
        this.noteService = noteService;
    }

    public List<Reclamation> getAllReclamations() {
        return reclamationRepository.findAllWithDetails();
    }

    // --- CRITICAL FIX: Update to use findByIdWithDetails() ---
    public Optional<Reclamation> getReclamationById(Long id) {
        return reclamationRepository.findByIdWithDetails(id); // Use the new query for single retrieval
    }
    // -----------------------------------------------------------

    @Transactional
    public Reclamation saveReclamation(Reclamation reclamation) {
        if (reclamation.getEtudiant() != null && reclamation.getEtudiant().getId() != null) {
            Etudiant etudiant = etudiantService.getEtudiantById(reclamation.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + reclamation.getEtudiant().getId()));
            reclamation.setEtudiant(etudiant);
        } else {
            throw new IllegalArgumentException("Etudiant ID is required for saving a reclamation.");
        }
        if (reclamation.getNote() != null && reclamation.getNote().getId() != null) {
            Note note = noteService.getNoteById(reclamation.getNote().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + reclamation.getNote().getId()));
            reclamation.setNote(note);
        } else {
            throw new IllegalArgumentException("Note ID is required for saving a reclamation.");
        }
        reclamation.setStatut("Pending");
        reclamation.setDateReclamation(LocalDate.now());
        return reclamationRepository.save(reclamation);
    }

    @Transactional
    public Reclamation updateReclamation(Long id, Reclamation reclamationDetails) {
        Reclamation existingReclamation = reclamationRepository.findByIdWithDetails(id) // <<< Use findByIdWithDetails here too for update
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));

        // Note: You should verify here if etudiant or note IDs are changed on update, and if so,
        // fetch the new managed entities and set them. The code below from previous updates
        // already handles this.
        if (reclamationDetails.getEtudiant() != null && reclamationDetails.getEtudiant().getId() != null &&
                (existingReclamation.getEtudiant() == null || !existingReclamation.getEtudiant().getId().equals(reclamationDetails.getEtudiant().getId()))) {
            Etudiant etudiant = etudiantService.getEtudiantById(reclamationDetails.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + reclamationDetails.getEtudiant().getId()));
            existingReclamation.setEtudiant(etudiant);
        }
        if (reclamationDetails.getNote() != null && reclamationDetails.getNote().getId() != null &&
                (existingReclamation.getNote() == null || !existingReclamation.getNote().getId().equals(reclamationDetails.getNote().getId()))) {
            Note note = noteService.getNoteById(reclamationDetails.getNote().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + reclamationDetails.getNote().getId()));
            existingReclamation.setNote(note);
        }

        existingReclamation.setRaison(reclamationDetails.getRaison());
        existingReclamation.setStatut(reclamationDetails.getStatut());

        return reclamationRepository.save(existingReclamation);
    }

    @Transactional
    public void deleteReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findByIdWithDetails(id) // <<< Use findByIdWithDetails here too
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        reclamationRepository.delete(reclamation);
    }

    @Transactional
    public Reclamation approveReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findByIdWithDetails(id) // <<< Use findByIdWithDetails here too
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        reclamation.setStatut("Approved");
        return reclamationRepository.save(reclamation);
    }

    @Transactional
    public Reclamation rejectReclamation(Long id) {
        Reclamation reclamation = reclamationRepository.findByIdWithDetails(id) // <<< Use findByIdWithDetails here too
                .orElseThrow(() -> new ResourceNotFoundException("Reclamation not found with id: " + id));
        reclamation.setStatut("Rejected");
        return reclamationRepository.save(reclamation);
    }
}
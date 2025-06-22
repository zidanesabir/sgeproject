package com.example.sgeproject.service;

import com.example.sgeproject.exception.ResourceNotFoundException;
import com.example.sgeproject.model.Etudiant;
import com.example.sgeproject.model.Examen;
import com.example.sgeproject.model.Note;
import com.example.sgeproject.repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final EtudiantService etudiantService;
    private final ExamenService examenService;

    public NoteService(NoteRepository noteRepository, EtudiantService etudiantService, ExamenService examenService) {
        this.noteRepository = noteRepository;
        this.etudiantService = etudiantService;
        this.examenService = examenService;
    }

    // --- CRITICAL FIX: Update to use findAllWithAllDetails() ---
    public List<Note> getAllNotes() {
        return noteRepository.findAllWithAllDetails(); // Use the new comprehensive query
    }
    // --------------------------------------------------------

    public Optional<Note> getNoteById(Long id) {
        return noteRepository.findById(id);
    }

    // --- CRITICAL FIX: Update to use findByEtudiantWithExamAndModule() ---
    public List<Note> getNotesByEtudiant(Etudiant etudiant) {
        return noteRepository.findByEtudiantWithExamAndModule(etudiant); // Use the custom query
    }
    // --------------------------------------------------------------------

    @Transactional
    public Note saveNote(Note note) {
        if (note.getEtudiant() != null && note.getEtudiant().getId() != null) {
            Etudiant etudiant = etudiantService.getEtudiantById(note.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + note.getEtudiant().getId()));
            note.setEtudiant(etudiant);
        }
        if (note.getExamen() != null && note.getExamen().getId() != null) {
            Examen examen = examenService.getExamenById(note.getExamen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + note.getExamen().getId()));
            note.setExamen(examen);
        }
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long id, Note noteDetails) {
        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));

        existingNote.setValeur(noteDetails.getValeur());
        existingNote.setDateSaisie(noteDetails.getDateSaisie());
        existingNote.setCommentaire(noteDetails.getCommentaire()); // Ensure these fields are handled if present
        existingNote.setDateModification(noteDetails.getDateModification());
        existingNote.setEstValidee(noteDetails.isEstValidee());

        if (noteDetails.getEtudiant() != null && noteDetails.getEtudiant().getId() != null &&
                (existingNote.getEtudiant() == null || !existingNote.getEtudiant().getId().equals(noteDetails.getEtudiant().getId()))) {
            Etudiant etudiant = etudiantService.getEtudiantById(noteDetails.getEtudiant().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id: " + noteDetails.getEtudiant().getId()));
            existingNote.setEtudiant(etudiant);
        }
        if (noteDetails.getExamen() != null && noteDetails.getExamen().getId() != null &&
                (existingNote.getExamen() == null || !existingNote.getExamen().getId().equals(noteDetails.getExamen().getId()))) {
            Examen examen = examenService.getExamenById(noteDetails.getExamen().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Examen not found with id: " + noteDetails.getExamen().getId()));
            existingNote.setExamen(examen);
        }
        return noteRepository.save(existingNote);
    }

    @Transactional
    public void deleteNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        noteRepository.delete(note);
    }
}
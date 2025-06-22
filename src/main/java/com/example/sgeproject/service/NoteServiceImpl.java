package com.example.sgeproject.service;

import com.example.sgeproject.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Override
    public int countByProfessorId(Long professorId) {
        return noteRepository.countByProfessorId(professorId);
    }
}

package com.clinic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.VisitNoteRequest;
import com.clinic.entity.Visit;
import com.clinic.entity.VisitNote;
import com.clinic.repository.VisitNoteRepository;
import com.clinic.repository.VisitRepository;

@Service
public class VisitNoteService {

    private final VisitNoteRepository visitNoteRepository;
    private final VisitRepository visitRepository;

    public VisitNoteService(
            VisitNoteRepository visitNoteRepository,
            VisitRepository visitRepository) {
        this.visitNoteRepository = visitNoteRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional
    public VisitNote saveNotes(VisitNoteRequest request) {

        Visit visit = visitRepository.findById(request.getVisitId())
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        if (visitNoteRepository.findByVisitId(visit.getId()).isPresent()) {
            throw new RuntimeException("Consultation notes already exist");
        }

        VisitNote note = new VisitNote();

        note.setVisitId(visit.getId());
        note.setSymptoms(request.getSymptoms());
        note.setDiagnosis(request.getDiagnosis());
        note.setPrescription(request.getPrescription());
        note.setAdvice(request.getAdvice());

        return visitNoteRepository.save(note);
    }
}
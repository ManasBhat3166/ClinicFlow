package com.clinic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.VisitRequest;
import com.clinic.entity.Token;
import com.clinic.entity.Visit;
import com.clinic.repository.TokenRepository;
import com.clinic.repository.VisitRepository;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final TokenRepository tokenRepository;

    public VisitService(
            VisitRepository visitRepository,
            TokenRepository tokenRepository) {
        this.visitRepository = visitRepository;
        this.tokenRepository = tokenRepository;
    }

    public List<Visit> getPatientVisitHistory(Long patientId) {

        return visitRepository.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    @Transactional
    public Visit startVisit(VisitRequest request) {

        Token token = tokenRepository.findById(request.getTokenId())
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!"IN_PROGRESS".equals(token.getStatus())) {
            throw new RuntimeException(
                    "Consultation can only start for an IN_PROGRESS token");
        }

        if (visitRepository.findByTokenId(token.getId()).isPresent()) {
            throw new RuntimeException(
                    "Visit already exists for this token");
        }

        Visit visit = new Visit();

        visit.setClinicId(token.getClinicId());
        visit.setTokenId(token.getId());
        visit.setPatientId(token.getPatientId());
        visit.setDoctorId(token.getDoctorId());
        visit.setVisitDate(LocalDate.now());

        return visitRepository.save(visit);
    }

    @Transactional
    public Visit completeVisit(Long visitId) {

        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        if (visit.getCompletedAt() != null) {
            throw new RuntimeException("Visit already completed");
        }

        Token token = tokenRepository.findById(visit.getTokenId())
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!"IN_PROGRESS".equals(token.getStatus())) {
            throw new RuntimeException("Token is not IN_PROGRESS");
        }

        visit.setCompletedAt(LocalDateTime.now());
        token.setStatus("COMPLETED");

        tokenRepository.save(token);

        return visitRepository.save(visit);
    }
}
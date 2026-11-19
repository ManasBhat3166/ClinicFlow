package com.clinic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.VisitRequest;
import com.clinic.entity.Visit;
import com.clinic.service.VisitService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PreAuthorize("hasRole('DOCTOR') and @clinicAccessService.hasTokenAccess(#p0.tokenId)")
    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public Visit startVisit(@Valid @RequestBody VisitRequest request) {
        return visitService.startVisit(request);
    }

    @PreAuthorize("hasRole('DOCTOR') and @clinicAccessService.hasVisitAccess(#p0)")
    @PatchMapping("/{visitId}/complete")
    public Visit completeVisit(@PathVariable Long visitId) {
        return visitService.completeVisit(visitId);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR') and @clinicAccessService.hasPatientAccess(#p0)")
    @GetMapping("/patient/{patientId}")
    public List<Visit> getPatientVisitHistory(
            @PathVariable Long patientId) {

        return visitService.getPatientVisitHistory(patientId);
    }
}
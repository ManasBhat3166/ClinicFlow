package com.clinic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.clinic.dto.VisitNoteRequest;
import com.clinic.entity.VisitNote;
import com.clinic.service.VisitNoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/visit-notes")
public class VisitNoteController {

    private final VisitNoteService visitNoteService;

    public VisitNoteController(VisitNoteService visitNoteService) {
        this.visitNoteService = visitNoteService;
    }

    @PreAuthorize("hasRole('DOCTOR') and @clinicAccessService.hasVisitAccess(#p0.visitId)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitNote saveNotes(@Valid @RequestBody VisitNoteRequest request) {
        return visitNoteService.saveNotes(request);
    }
}
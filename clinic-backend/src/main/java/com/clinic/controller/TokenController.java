package com.clinic.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.TokenRequest;
import com.clinic.entity.Token;
import com.clinic.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PreAuthorize("hasAnyRole('OWNER', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0.clinicId)")
    @PostMapping
    public Token generateToken(@Valid @RequestBody TokenRequest request) {
        return tokenService.generateToken(request);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0) and @clinicAccessService.hasDoctorAccess(#p1)")
    @GetMapping("/queue")
    public List<Token> getDoctorQueue(
            @RequestParam Long clinicId,
            @RequestParam Long doctorId) {

        return tokenService.getDoctorQueue(clinicId, doctorId);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0) and @clinicAccessService.hasDoctorAccess(#p1)")
    @PostMapping("/call-next")
    public Token callNextPatient(
            @RequestParam Long clinicId,
            @RequestParam Long doctorId) {

        return tokenService.callNextPatient(clinicId, doctorId);
    }

    @PreAuthorize("hasAnyRole('DOCTOR', 'RECEPTION') and @clinicAccessService.hasTokenAccess(#p0)")
    @PatchMapping("/{tokenId}/status")
    public Token updateTokenStatus(
            @PathVariable Long tokenId,
            @RequestParam String status) {

        return tokenService.updateTokenStatus(tokenId, status);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/clinic-queue")
    public List<Token> getClinicQueue(@RequestParam Long clinicId) {
        return tokenService.getClinicQueue(clinicId);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/clinic-statistics")
    public Map<String, Long> getClinicQueueStatistics(
            @RequestParam Long clinicId) {

        return tokenService.getClinicQueueStatistics(clinicId);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0) and @clinicAccessService.hasDoctorAccess(#p1)")
    @GetMapping("/currently-called")
    public Token getCurrentlyCalledToken(
            @RequestParam Long clinicId,
            @RequestParam Long doctorId) {

        return tokenService.getCurrentlyCalledToken(clinicId, doctorId);
    }

    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/clinic-called")
    public List<Token> getClinicCalledTokens(
            @RequestParam Long clinicId) {

        return tokenService.getClinicCalledTokens(clinicId);
    }
}
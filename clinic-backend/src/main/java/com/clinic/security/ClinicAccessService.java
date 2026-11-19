package com.clinic.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.entity.Token;
import com.clinic.entity.Visit;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.TokenRepository;
import com.clinic.repository.VisitRepository;

@Service
public class ClinicAccessService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenRepository tokenRepository;
    private final VisitRepository visitRepository;

    public ClinicAccessService(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            TokenRepository tokenRepository,
            VisitRepository visitRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenRepository = tokenRepository;
        this.visitRepository = visitRepository;
    }

    public boolean hasClinicAccess(Long clinicId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser user)) {
            return false;
        }

        return user.clinicId().equals(clinicId);
    }

    public boolean hasUserAccess(Long userId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser user)) {
            return false;
        }

        return user.userId().equals(userId);
    }

    public boolean hasPatientAccess(Long patientId) {

        Patient patient = patientRepository.findById(patientId).orElse(null);

        return patient != null && hasClinicAccess(patient.getClinicId());
    }

    public boolean hasDoctorAccess(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        return doctor != null && hasClinicAccess(doctor.getClinicId());
    }

    public boolean hasTokenAccess(Long tokenId) {

        Token token = tokenRepository.findById(tokenId).orElse(null);

        return token != null && hasClinicAccess(token.getClinicId());
    }

    public boolean hasVisitAccess(Long visitId) {

        Visit visit = visitRepository.findById(visitId).orElse(null);

        return visit != null && hasClinicAccess(visit.getClinicId());
    }
}
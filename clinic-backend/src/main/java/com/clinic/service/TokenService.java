package com.clinic.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clinic.dto.TokenRequest;
import com.clinic.entity.ClinicSetting;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.entity.Token;
import com.clinic.repository.ClinicSettingRepository;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.PatientRepository;
import com.clinic.repository.TokenRepository;
@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicSettingRepository clinicSettingRepository;

    public TokenService(
            TokenRepository tokenRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            ClinicSettingRepository clinicSettingRepository) {

        this.tokenRepository = tokenRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.clinicSettingRepository = clinicSettingRepository;
    }

    public Map<String, Long> getClinicQueueStatistics(Long clinicId) {

        LocalDate today = LocalDate.now();

        Map<String, Long> statistics = new HashMap<>();

        statistics.put("WAITING",
                tokenRepository.countByClinicIdAndTokenDateAndStatus(
                        clinicId, today, "WAITING"));

        statistics.put("CALLED",
                tokenRepository.countByClinicIdAndTokenDateAndStatus(
                        clinicId, today, "CALLED"));

        statistics.put("IN_PROGRESS",
                tokenRepository.countByClinicIdAndTokenDateAndStatus(
                        clinicId, today, "IN_PROGRESS"));

        statistics.put("COMPLETED",
                tokenRepository.countByClinicIdAndTokenDateAndStatus(
                        clinicId, today, "COMPLETED"));

        statistics.put("SKIPPED",
                tokenRepository.countByClinicIdAndTokenDateAndStatus(
                        clinicId, today, "SKIPPED"));

        return statistics;
    }

    // Generate a new token
    @Transactional
    public Token generateToken(TokenRequest request) {

        // Lock the doctor record while generating a token
        Doctor doctor = doctorRepository.findByIdForUpdate(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!doctor.getClinicId().equals(request.getClinicId())) {
            throw new RuntimeException("Doctor does not belong to this clinic");
        }

        if (!doctor.isActive()) {
            throw new RuntimeException("Doctor is inactive");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!patient.getClinicId().equals(request.getClinicId())) {
            throw new RuntimeException("Patient does not belong to this clinic");
        }

        String source = request.getSource().toUpperCase();

        if (!source.equals("WALK_IN") && !source.equals("ONLINE")) {
            throw new RuntimeException("Source must be WALK_IN or ONLINE");
        }

        if ("ONLINE".equals(source)) {
            validateOnlineBooking(request.getClinicId());
        }

        LocalDate today = LocalDate.now();

        Token lastToken = tokenRepository
                .findTopByClinicIdAndDoctorIdAndTokenDateOrderByTokenNumberDesc(
                        request.getClinicId(),
                        request.getDoctorId(),
                        today
                )
                .orElse(null);

        int nextTokenNumber = (lastToken == null)
                ? 1
                : lastToken.getTokenNumber() + 1;

        Token token = new Token();

        token.setClinicId(request.getClinicId());
        token.setPatientId(request.getPatientId());
        token.setDoctorId(request.getDoctorId());
        token.setTokenNumber(nextTokenNumber);
        token.setTokenDate(today);
        token.setSource(source);
        token.setStatus("WAITING");

        return tokenRepository.save(token);
    }

    // Get daily queue for a doctor
    public List<Token> getDoctorQueue(Long clinicId, Long doctorId) {

        return tokenRepository
                .findByClinicIdAndDoctorIdAndTokenDateOrderByTokenNumberAsc(
                        clinicId,
                        doctorId,
                        LocalDate.now()
                );
    }

    public List<Token> getClinicQueue(Long clinicId) {

        return tokenRepository
                .findByClinicIdAndTokenDateOrderByDoctorIdAscTokenNumberAsc(
                        clinicId,
                        LocalDate.now()
                );
    }

    // Call next waiting patient
    @Transactional
    public Token callNextPatient(Long clinicId, Long doctorId) {

        Doctor doctor = doctorRepository.findByIdForUpdate(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!doctor.getClinicId().equals(clinicId)) {
            throw new RuntimeException("Doctor does not belong to this clinic");
        }

        if (!doctor.isActive()) {
            throw new RuntimeException("Doctor is inactive");
        }

        Token token = tokenRepository
                .findFirstByClinicIdAndDoctorIdAndTokenDateAndStatusOrderByTokenNumberAsc(
                        clinicId,
                        doctorId,
                        LocalDate.now(),
                        "WAITING"
                )
                .orElseThrow(() -> new RuntimeException("No patients waiting"));

        token.setStatus("CALLED");

        return tokenRepository.save(token);
    }

    // Update token status
    @Transactional
    public Token updateTokenStatus(Long tokenId, String newStatus) {

        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        String currentStatus = token.getStatus();
        newStatus = newStatus.toUpperCase();

        boolean validTransition = false;

        if (currentStatus.equals("CALLED")
                && newStatus.equals("IN_PROGRESS")) {
            validTransition = true;
        }

        else if (currentStatus.equals("IN_PROGRESS")
                && newStatus.equals("COMPLETED")) {
            validTransition = true;
        }

        else if ((currentStatus.equals("WAITING")
                || currentStatus.equals("CALLED"))
                && newStatus.equals("SKIPPED")) {
            validTransition = true;
        }

        else if (currentStatus.equals("CALLED")
                && newStatus.equals("NO_SHOW")) {
            validTransition = true;
        }

        else if (currentStatus.equals("WAITING")
                && newStatus.equals("CANCELLED")) {
            validTransition = true;
        }

        if (!validTransition) {
            throw new RuntimeException(
                    "Invalid status transition: " + currentStatus + " to " + newStatus
            );
        }

        token.setStatus(newStatus);

        return tokenRepository.save(token);
    }

    private void validateOnlineBooking(Long clinicId) {

        ClinicSetting settings = clinicSettingRepository
                .findByClinicId(clinicId)
                .orElseThrow(() -> new RuntimeException(
                        "Clinic settings not found"));

        if (!Boolean.TRUE.equals(settings.getOnlineBooking())) {
            throw new RuntimeException("Online booking is disabled");
        }

        long totalTokens = tokenRepository.countByClinicIdAndTokenDate(
                clinicId, LocalDate.now());

        if (totalTokens >= settings.getDailyTokenLimit()) {
            throw new RuntimeException("Daily token limit reached");
        }

        LocalTime currentTime = LocalTime.now();

        if (settings.getOpdStart() == null || settings.getOpdEnd() == null
                || currentTime.isBefore(settings.getOpdStart())
                || currentTime.isAfter(settings.getOpdEnd())) {
            throw new RuntimeException("Online booking is outside OPD hours");
        }
    }

    public Token getCurrentlyCalledToken(Long clinicId, Long doctorId) {

        return tokenRepository
                .findFirstByClinicIdAndDoctorIdAndTokenDateAndStatusOrderByTokenNumberAsc(
                        clinicId,
                        doctorId,
                        LocalDate.now(),
                        "CALLED"
                )
                .orElse(null);
    }

    public List<Token> getClinicCalledTokens(Long clinicId) {

        return tokenRepository
                .findByClinicIdAndTokenDateAndStatusOrderByDoctorIdAscTokenNumberAsc(
                        clinicId,
                        LocalDate.now(),
                        "CALLED"
                );
    }
}
package com.clinic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.dto.PatientRequest;
import com.clinic.entity.Patient;
import com.clinic.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Patient registerPatient(PatientRequest request) {

        Patient patient = new Patient();

        patient.setClinicId(request.getClinicId());
        patient.setName(request.getName());
        patient.setMobile(request.getMobile());
        patient.setDob(request.getDob());
        patient.setGender(request.getGender());

        return patientRepository.save(patient);
    }

    public List<Patient> getPatientsByClinic(Long clinicId) {
        return patientRepository.findByClinicId(clinicId);
    }

    public Patient searchPatient(Long clinicId, String mobile) {
        return patientRepository
                .findByClinicIdAndMobile(clinicId, mobile)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
}
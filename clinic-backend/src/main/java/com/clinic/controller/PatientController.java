package com.clinic.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.PatientRequest;
import com.clinic.entity.Patient;
import com.clinic.security.ClinicAccessService;
import com.clinic.service.PatientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final ClinicAccessService clinicAccessService;

    public PatientController(
            PatientService patientService,
            ClinicAccessService clinicAccessService) {

        this.patientService = patientService;
        this.clinicAccessService = clinicAccessService;
    }  

    // Register patient
    @PreAuthorize("hasAnyRole('OWNER', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0.clinicId)")
    @PostMapping
    public Patient registerPatient(@Valid @RequestBody PatientRequest request) {
        return patientService.registerPatient(request);
    }

    // Get all patients of a clinic
    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/clinic/{clinicId}")
    public List<Patient> getPatients(@PathVariable Long clinicId) {
        return patientService.getPatientsByClinic(clinicId);
    }

    // Search patient by mobile number
    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/search")
    public Patient searchPatient(
            @RequestParam Long clinicId,
            @RequestParam String mobile) {

        return patientService.searchPatient(clinicId, mobile);
    }

    // Get patient by ID
    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasPatientAccess(#p0)")
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }
}
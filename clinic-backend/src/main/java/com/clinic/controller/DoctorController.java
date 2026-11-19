package com.clinic.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.DoctorRequest;
import com.clinic.dto.DoctorUpdateRequest;
import com.clinic.entity.Doctor;
import com.clinic.service.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Create doctor
    @PreAuthorize("hasRole('OWNER') and @clinicAccessService.hasClinicAccess(#p0.clinicId)")
    @PostMapping
    public Doctor createDoctor(@Valid @RequestBody DoctorRequest request) {
        return doctorService.createDoctor(request);
    }

    // Get all doctors by clinic
    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasClinicAccess(#p0)")
    @GetMapping("/clinic/{clinicId}")
    public List<Doctor> getDoctors(@PathVariable Long clinicId) {
        return doctorService.getDoctorsByClinic(clinicId);
    }

    // Get doctor by ID
    @PreAuthorize("hasAnyRole('OWNER', 'DOCTOR', 'RECEPTION') and @clinicAccessService.hasDoctorAccess(#p0)")
    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    // Update doctor
    @PreAuthorize("hasRole('OWNER') and @clinicAccessService.hasDoctorAccess(#p0)")
    @PutMapping("/{id}")
    public Doctor updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorUpdateRequest request) {

        return doctorService.updateDoctor(id, request);
    }

    // Activate or deactivate doctor
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{id}/status")
    public Doctor updateDoctorStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return doctorService.updateDoctorStatus(id, active);
    }
}
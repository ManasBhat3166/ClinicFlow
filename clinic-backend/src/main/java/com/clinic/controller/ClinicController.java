package com.clinic.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.dto.ClinicRequest;
import com.clinic.entity.Clinic;
import com.clinic.service.ClinicService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping
    public Clinic createClinic(
            @Valid @RequestBody ClinicRequest request) {

        return clinicService.createClinic(request);
    }

    @GetMapping
    public List<Clinic> getAllClinics() {
        return clinicService.getAllClinics();
    }

    @GetMapping("/{id}")
    public Clinic getClinicById(@PathVariable Long id) {
        return clinicService.getClinicById(id);
    }

    @PutMapping("/{id}")
    public Clinic updateClinic(
            @PathVariable Long id,
            @Valid @RequestBody ClinicRequest request) {

        return clinicService.updateClinic(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteClinic(@PathVariable Long id) {

        clinicService.deleteClinic(id);

        return "Clinic deleted successfully";
    }
}

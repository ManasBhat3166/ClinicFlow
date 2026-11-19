
package com.clinic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clinic.dto.ClinicRequest;
import com.clinic.entity.Clinic;
import com.clinic.repository.ClinicRepository;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public Clinic createClinic(ClinicRequest request) {

        Clinic clinic = new Clinic();

        clinic.setName(request.getName());
        clinic.setAddress(request.getAddress());
        clinic.setPhone(request.getPhone());

        return clinicRepository.save(clinic);
    }

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public Clinic getClinicById(Long id) {
        return clinicRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Clinic not found"));
    }

    public Clinic updateClinic(Long id, ClinicRequest request) {

        Clinic clinic = getClinicById(id);

        clinic.setName(request.getName());
        clinic.setAddress(request.getAddress());
        clinic.setPhone(request.getPhone());

        return clinicRepository.save(clinic);
    }

    public void deleteClinic(Long id) {

        Clinic clinic = getClinicById(id);

        clinicRepository.delete(clinic);
    }
}

package com.clinic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinic.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByClinicId(Long clinicId);

    Optional<Patient> findByClinicIdAndMobile(Long clinicId, String mobile);
}
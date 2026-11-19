package com.clinic.service;

import com.clinic.dto.DoctorRequest;
import com.clinic.dto.DoctorUpdateRequest;
import com.clinic.entity.Doctor;
import com.clinic.entity.User;
import com.clinic.repository.ClinicRepository;
import com.clinic.repository.DoctorRepository;
import com.clinic.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public DoctorService(
            DoctorRepository doctorRepository,
            UserRepository userRepository,
            ClinicRepository clinicRepository) {

        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
    }

    // Create doctor and linked user account
    @Transactional
    public Doctor createDoctor(DoctorRequest request) {

        if (!clinicRepository.existsById(request.getClinicId())) {
            throw new RuntimeException("Clinic not found");
        }

        if (userRepository.findByClinicIdAndMobile(
                request.getClinicId(), request.getMobile()).isPresent()) {
            throw new RuntimeException("Mobile number already registered");
        }

        User user = new User();
        user.setClinicId(request.getClinicId());
        user.setName(request.getName());
        user.setMobile(request.getMobile());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("DOCTOR");
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setClinicId(request.getClinicId());
        doctor.setUserId(savedUser.getId());
        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setActive(true);

        return doctorRepository.save(doctor);
    }

    // Get all doctors of a clinic
    public List<Doctor> getDoctorsByClinic(Long clinicId) {
        return doctorRepository.findByClinicId(clinicId);
    }

    // Get doctor by ID
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    // Update doctor details
    @Transactional
    public Doctor updateDoctor(Long id, DoctorUpdateRequest request) {

        Doctor doctor = getDoctorById(id);

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new RuntimeException("Doctor user not found"));

        doctor.setName(request.getName());
        doctor.setSpecialization(request.getSpecialization());

        user.setName(request.getName());

        userRepository.save(user);

        return doctorRepository.save(doctor);
    }

    // Activate or deactivate doctor
    @Transactional
    public Doctor updateDoctorStatus(Long id, boolean active) {

        Doctor doctor = getDoctorById(id);

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new RuntimeException("Doctor user not found"));

        doctor.setActive(active);
        user.setActive(active);

        userRepository.save(user);

        return doctorRepository.save(doctor);
    }
}
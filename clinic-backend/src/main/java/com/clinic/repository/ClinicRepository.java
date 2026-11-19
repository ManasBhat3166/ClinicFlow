package com.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.entity.Clinic;

@Repository
public interface ClinicRepository
        extends JpaRepository<Clinic, Long> {

}

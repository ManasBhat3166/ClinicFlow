package com.clinic.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.entity.Visit;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    Optional<Visit> findByTokenId(Long tokenId);

    List<Visit> findByPatientIdOrderByVisitDateDesc(Long patientId);

    List<Visit> findByClinicId(Long clinicId);
}
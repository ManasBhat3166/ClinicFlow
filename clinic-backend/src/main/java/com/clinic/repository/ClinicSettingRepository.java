package com.clinic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.entity.ClinicSetting;

@Repository
public interface ClinicSettingRepository
        extends JpaRepository<ClinicSetting, Long> {

    Optional<ClinicSetting> findByClinicId(Long clinicId);
}
package com.clinic.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinic.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    long countByClinicIdAndTokenDateAndStatus(
        Long clinicId,
        LocalDate tokenDate,
        String status
    );  

    List<Token> findByClinicIdAndDoctorIdAndTokenDateOrderByTokenNumberAsc(
            Long clinicId,
            Long doctorId,
            LocalDate tokenDate
    );

    Optional<Token> findTopByClinicIdAndDoctorIdAndTokenDateOrderByTokenNumberDesc(
            Long clinicId,
            Long doctorId,
            LocalDate tokenDate
    );

    Optional<Token> findFirstByClinicIdAndDoctorIdAndTokenDateAndStatusOrderByTokenNumberAsc(
        Long clinicId,
        Long doctorId,
        LocalDate tokenDate,
        String status
    );

    List<Token> findByClinicIdAndTokenDateAndStatusOrderByDoctorIdAscTokenNumberAsc(
        Long clinicId,
        LocalDate tokenDate,
        String status
    );

    long countByClinicIdAndTokenDate(
        Long clinicId,
        LocalDate tokenDate
    );

    List<Token> findByClinicIdAndTokenDateOrderByDoctorIdAscTokenNumberAsc(
        Long clinicId,
        LocalDate tokenDate
    );
}
package com.clinic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.clinic.entity.VisitNote;

@Repository
public interface VisitNoteRepository extends JpaRepository<VisitNote, Long> {

    Optional<VisitNote> findByVisitId(Long visitId);
}
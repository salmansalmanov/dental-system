package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Page<Patient> findAllByStatusNot(EntityStatus status, Pageable pageable);

    Optional<Patient> findByIdAndStatusNot(UUID id, EntityStatus status);
}

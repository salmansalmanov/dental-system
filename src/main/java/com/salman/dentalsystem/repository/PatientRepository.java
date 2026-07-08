package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByIdAndStatusNot(UUID id, EntityStatus status);

    Page<Patient> findAllByStatus(EntityStatus status, Pageable pageable);

    @Query("""
                SELECT p
                FROM Patient p
                WHERE (:status IS NULL OR p.status = :status)
                  AND (
                       :search IS NULL
                       OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                       OR LOWER(p.surname) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                       OR LOWER(p.patronymic) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                       OR LOWER(p.phoneNumber) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                       OR LOWER(p.pin) LIKE CONCAT('%', LOWER(CAST(:search AS string)), '%')
                  )
            """)
    Page<Patient> findAllFiltered(
            @Param("search") String search,
            @Param("status") EntityStatus status,
            Pageable pageable
    );
}

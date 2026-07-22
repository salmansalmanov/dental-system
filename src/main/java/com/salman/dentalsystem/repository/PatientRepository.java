package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByIdAndStatus(UUID id, EntityStatus status);

    @Query("""
    SELECT p FROM Patient p
    WHERE (:status IS NULL OR p.status = :status)
      AND (
           :search IS NULL 
           OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(p.surname) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(COALESCE(p.patronymic, '')) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(CONCAT(p.name, ' ', p.surname)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(CONCAT(p.surname, ' ', p.name)) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
           OR LOWER(CONCAT(p.name, ' ', p.surname, ' ', COALESCE(p.patronymic, ''))) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
      )
""")
    Page<Patient> findAllFiltered(
            @Param("search") String search,
            @Param("status") EntityStatus status,
            Pageable pageable
    );

    List<Patient> findAllByStatusAndDeletedAtBefore(EntityStatus status, LocalDateTime threshold);

    List<Patient> findAllByStatus(EntityStatus status);
}

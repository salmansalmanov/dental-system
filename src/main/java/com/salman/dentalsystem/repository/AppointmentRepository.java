package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    @EntityGraph(attributePaths = "patient")
    Page<Appointment> findAllByStatusNot(EntityStatus status, Pageable pageable);

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient
            JOIN FETCH a.doctor
            WHERE a.id = :id AND a.status <> :status
            """)
    Optional<Appointment> findByIdAndStatusNot(@Param("id") UUID id, @Param("status") EntityStatus status);
}

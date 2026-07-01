package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Page<Appointment> findAllByStatusNot(EntityStatus status, Pageable pageable);

    Optional<Appointment> findByIdAndStatusNot(UUID id, EntityStatus status);
}

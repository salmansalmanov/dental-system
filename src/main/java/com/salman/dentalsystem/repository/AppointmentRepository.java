package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
}

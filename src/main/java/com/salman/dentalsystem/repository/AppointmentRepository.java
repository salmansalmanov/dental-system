package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    boolean existsByDentistIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID dentistId,
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );

    boolean existsByPatientIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID patientId,
            LocalDate date,
            LocalTime endTime,
            LocalTime startTime
    );
}

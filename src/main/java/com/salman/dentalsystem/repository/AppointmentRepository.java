package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.XrayAgent;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
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

    @NullMarked
    @EntityGraph(attributePaths = "dentist")
    Optional<Appointment> findById(UUID id);

    List<Appointment> findAllByPatientId(UUID patientId);

    List<Appointment> findAllByPatientIdAndStatus(UUID patientId, EntityStatus status);

//    Page<Appointment> findAllByPatientIdAndStatus(UUID patientId, EntityStatus status, Pageable pageable);

    @Query("""
    SELECT a
    FROM Appointment a
    WHERE a.patient.id = :patientId
      AND (:status IS NULL OR a.status = :status)
    """)
    Page<Appointment> findAllByPatientIdAndStatus(
            @Param("patientId") UUID patientId,
            @Param("status") EntityStatus status,
            Pageable pageable
    );

    boolean existsByDentistIdAndDateAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID dentistId,
            LocalDate date,
            UUID appointmentId,
            LocalTime endTime,
            LocalTime startTime
    );

    boolean existsByPatientIdAndDateAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID patientId,
            LocalDate date,
            UUID appointmentId,
            LocalTime endTime,
            LocalTime startTime
    );

    List<Appointment> findAllByStatusAndDeletedAtBefore(EntityStatus status, LocalDateTime threshold);

    List<Appointment> findAllByPatientIdInAndStatus(List<UUID> patientIds, EntityStatus status);

    Long countByDate(LocalDate date);

    @Query("""
                SELECT a
                FROM Appointment a
                JOIN a.dentist d
                WHERE d.xrayAgent = :xrayAgent
                  AND a.date = :date
                  AND :currentTime >= a.startTime
                  AND :currentTime < a.endTime
                  AND a.status = :status
            """)
    Optional<Appointment> findCurrentAppointment(
            XrayAgent xrayAgent,
            LocalDate date,
            LocalTime currentTime,
            EntityStatus status
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.date = CURRENT_DATE AND a.status = com.salman.dentalsystem.model.enums.EntityStatus.ACTIVE " +
            "ORDER BY a.startTime")
    Page<Appointment> findAllForToday(Pageable pageable);
}

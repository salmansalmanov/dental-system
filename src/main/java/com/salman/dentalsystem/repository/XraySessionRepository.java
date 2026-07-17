package com.salman.dentalsystem.repository;

import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.XraySession;
import com.salman.dentalsystem.model.enums.XraySessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface XraySessionRepository extends JpaRepository<XraySession, UUID> {
    Optional<XraySession> findByStatus(XraySessionStatus status);

    boolean existsByAppointmentAndStatus(Appointment appointment, XraySessionStatus status);

    List<XraySession> findAllByStatusAndExpiresAtBefore(XraySessionStatus status, LocalDateTime now);

    List<XraySession> findAllByAppointment(Appointment appointment);
}

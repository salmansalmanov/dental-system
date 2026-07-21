package com.salman.dentalsystem.scheduler;

import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.AppointmentStatus;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SoftDeleteEntityPurgeScheduler {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    private static final Duration RETENTION_PERIOD = Duration.ofDays(30);

    @Scheduled(cron = "0 30 1 * * *")
    @Transactional
    public void purgeDeletedPatients() {
        LocalDateTime threshold = LocalDateTime.now().minus(RETENTION_PERIOD);
        List<Patient> patients = patientRepository.findAllByStatusAndDeletedAtBefore(EntityStatus.DELETED, threshold);
        if (patients.isEmpty()) {
            return;
        }

        patients.forEach(patient -> patient.setStatus(EntityStatus.TRASH));
        patientRepository.saveAll(patients);

        List<UUID> patientIds = patients.stream().map(Patient::getId).toList();
        List<Appointment> appointments = appointmentRepository.findAllByPatientIdInAndStatus(patientIds, EntityStatus.DELETED);
        if (!appointments.isEmpty()) {
            appointments.forEach(appointment -> {
                appointment.setStatus(EntityStatus.TRASH);
                appointment.setAppointmentStatus(AppointmentStatus.TRASH);
            });
            appointmentRepository.saveAll(appointments);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void purgeDeletedAppointments() {
        LocalDateTime threshold = LocalDateTime.now().minus(RETENTION_PERIOD);
        List<Appointment> appointments = appointmentRepository.findAllByStatusAndDeletedAtBefore(EntityStatus.DELETED, threshold);
        if (appointments.isEmpty()) {
            return;
        }

        appointments.forEach(appointment -> {
            appointment.setStatus(EntityStatus.TRASH);
            appointment.setAppointmentStatus(AppointmentStatus.TRASH);
        });
        appointmentRepository.saveAll(appointments);
    }
}
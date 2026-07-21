package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.ConflictException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.PatientMapper;
import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.request.PatientUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PatientCountResponse;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.AppointmentStatus;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.repository.PatientRepository;
import com.salman.dentalsystem.result.*;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
import com.salman.dentalsystem.service.abstraction.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientMapper patientMapper;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;

    @Override
    public DataResult<PatientDetailedResponse> create(PatientCreateRequest request) {
        Patient patient = patientMapper.createRequestToEntity(request);
        patient.setStatus(EntityStatus.ACTIVE);
        Patient savedPatient = patientRepository.save(patient);
        PatientDetailedResponse patientDetailedResponse = patientMapper.toDetailedResponse(savedPatient);
        return new SuccessDataResult<>(patientDetailedResponse, "Patient created successfully");
    }

    @Override
    public DataResult<PatientDetailedResponse> getById(UUID id) {
        Patient foundPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        PatientDetailedResponse patientDetailedResponse = patientMapper.toDetailedResponse(foundPatient);
        return new SuccessDataResult<>(patientDetailedResponse, "Patient found successfully");
    }

    @Override
    public DataResult<PageData<PatientResponse>> getAll(String search, EntityStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Patient> patientPage = patientRepository.findAllFiltered(search, status, pageable);
        PageData<PatientResponse> pageData = PageData.<PatientResponse>builder()
                .totalPages(patientPage.getTotalPages())
                .totalElements(patientPage.getTotalElements())
                .firstPage(patientPage.isFirst())
                .lastPage(patientPage.isLast())
                .page(patientPage.getNumber())
                .size(patientPage.getSize())
                .content(patientPage.getContent().stream().map(patientMapper::toResponse).toList())
                .build();
        return new SuccessDataResult<>(pageData, "Patients found successfully");
    }

    @Override
    public DataResult<PatientDetailedResponse> updateById(UUID id, PatientUpdateRequest request) {
        Patient existingPatient = patientRepository.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        Patient updatedPatient = patientMapper.updateRequestToEntity(request, existingPatient);
        Patient savedPatient = patientRepository.save(updatedPatient);
        return new SuccessDataResult<>(patientMapper.toDetailedResponse(savedPatient), "Patient updated successfully");
    }

    @Override
    @Transactional
    public DataResult<PatientDetailedResponse> deactivateById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        patient.setStatus(EntityStatus.DELETED);
        patient.setDeletedAt(LocalDateTime.now());
        List<Appointment> appointments = appointmentRepository.findAllByPatientId(patient.getId());
        appointments.forEach(appointment -> {
            appointment.setStatus(EntityStatus.DELETED);
            appointment.setAppointmentStatus(AppointmentStatus.DELETED);
            appointment.setDeletedAt(LocalDateTime.now());
        });
        appointmentRepository.saveAll(appointments);
        Patient savedPatient = patientRepository.save(patient);
        return new SuccessDataResult<>(patientMapper.toDetailedResponse(savedPatient), "Patient deleted successfully");
    }

    @Override
    public DataResult<PatientDetailedResponse> activateById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deleted patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        if (patient.getStatus() == EntityStatus.ACTIVE) {
            throw new ConflictException("Patient is already active", ErrorCode.PATIENT_ALREADY_ACTIVE);
        }
        List<Appointment> appointments = appointmentRepository.findAllByPatientIdAndStatusIn(patient.getId(), List.of(EntityStatus.DELETED, EntityStatus.TRASH));
        appointments.forEach(appointment -> {
            appointment.setStatus(EntityStatus.ACTIVE);
            appointment.setAppointmentStatus(appointmentService.initializeAppointmentStatus(appointment.getDate(), appointment.getStartTime(), appointment.getEndTime()));
            appointment.setDeletedAt(null);
        });
        patient.setStatus(EntityStatus.ACTIVE);
        patient.setDeletedAt(null);
        appointmentRepository.saveAll(appointments);
        Patient savedPatient = patientRepository.save(patient);
        return new SuccessDataResult<>(patientMapper.toDetailedResponse(savedPatient), "Patient activated successfully");
    }

    @Override
    public DataResult<PatientCountResponse> getAllPatientsCount() {
        Long count = patientRepository.count();
        PatientCountResponse response = new PatientCountResponse(count);
        return new SuccessDataResult<>(response, "Patient count found successfully");
    }

    @Override
    @Transactional
    public Result deleteById(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id, ErrorCode.PATIENT_NOT_FOUND));
        List<Appointment> appointments = appointmentRepository.findAllByPatientIdAndStatusIn(patient.getId(), List.of(EntityStatus.TRASH));
        appointmentRepository.deleteAll(appointments);
        patientRepository.deleteById(id);
        return new SuccessResult("Patient deleted successfully");
    }

    @Override
    @Transactional
    public Result deleteAllTrashed() {
        List<Patient> patients = patientRepository.findAllByStatus(EntityStatus.TRASH);
        for (Patient patient : patients) {
            List<Appointment> appointments = appointmentRepository.findAllByPatientIdAndStatusIn(patient.getId(), List.of(EntityStatus.TRASH));
            appointmentRepository.deleteAll(appointments);
            patientRepository.deleteById(patient.getId());
        }
        return new SuccessResult("All patients deleted successfully");
    }
}

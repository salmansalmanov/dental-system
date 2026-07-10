package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.ConflictException;
import com.salman.dentalsystem.exception.custom.InvalidInputException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.AppointmentMapper;
import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
import com.salman.dentalsystem.service.abstraction.PatientService;
import com.salman.dentalsystem.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final UserService userService;
    private final PatientService patientService;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    public DataResult<AppointmentDetailedResponse> create(AppointmentCreateRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidInputException("End time must be after start time", ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        validateDentistAvailability(request);
        validatePatientAvailability(request);
        User dentist = userService.getDentistById(request.getDentistId());
        Patient patient = patientService.getActivePatientById(request.getPatientId());
        Appointment appointment = appointmentMapper.createRequestToEntity(request);
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setStatus(EntityStatus.ACTIVE);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment created successfully");
    }

    @Override
    public DataResult<AppointmentDetailedResponse> getById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(appointment);
        return new SuccessDataResult<>(response, "Appointment found successfully");
    }

    @Override
    public DataResult<PageData<AppointmentResponse>> getAllByPatientId(UUID patientId, int page, int size) {
        Patient patient = patientService.getPatientById(patientId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Appointment> appointmentPage = appointmentRepository.findAllByPatientId(patient.getId(), pageable);
        PageData<AppointmentResponse> pageData = new PageData<>(
                appointmentPage.getTotalPages(),
                appointmentPage.getTotalElements(),
                appointmentPage.isFirst(),
                appointmentPage.isLast(),
                appointmentPage.getNumber(),
                appointmentPage.getSize(),
                appointmentPage.getContent().stream().map(appointmentMapper::toResponse).toList()
        );
        return new SuccessDataResult<>(pageData, "Appointments found successfully");
    }

    private void validateDentistAvailability(AppointmentCreateRequest request) {
        boolean hasConflict = appointmentRepository.existsByDentistIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getDentistId(),
                request.getDate(),
                request.getEndTime(),
                request.getStartTime()
        );
        if (hasConflict) {
            throw new ConflictException("Dentist already has another appointment during this time", ErrorCode.APPOINTMENT_CONFLICT);
        }
    }

    private void validatePatientAvailability(AppointmentCreateRequest request) {
        boolean hasConflict = appointmentRepository.existsByPatientIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getPatientId(),
                request.getDate(),
                request.getEndTime(),
                request.getStartTime()
        );
        if (hasConflict) {
            throw new ConflictException("Patient already has another appointment during this time", ErrorCode.APPOINTMENT_CONFLICT);
        }
    }
}

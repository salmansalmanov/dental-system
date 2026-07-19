package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.ConflictException;
import com.salman.dentalsystem.exception.custom.InvalidInputException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.AppointmentMapper;
import com.salman.dentalsystem.mapper.XrayImageMapper;
import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentTodayCountResponse;
import com.salman.dentalsystem.model.dto.response.XrayImageResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.entity.XrayImage;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.repository.XrayImageRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final UserService userService;
    private final PatientService patientService;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;
    private final XrayImageRepository xrayImageRepository;
    private final XrayImageMapper xrayImageMapper;

    @Override
    public DataResult<AppointmentDetailedResponse> create(UUID patientId, AppointmentCreateRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidInputException("End time must be after start time", ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        User currentUser = userService.getCurrentUser();
        if (currentUser.getStatus() != EntityStatus.ACTIVE || (currentUser.getRole() != Role.DENTIST && currentUser.getRole() != Role.ADMIN)) {
            throw new InvalidInputException("Only active dentists or admins can create appointments", ErrorCode.UNAUTHORIZED_ACTION);
        }

        validateDentistAvailabilityForCreate(currentUser.getId(), request);
        validatePatientAvailabilityForCreate(patientId, request);
        Patient patient = patientService.getActivePatientById(patientId);
        Appointment appointment = appointmentMapper.createRequestToEntity(request);
        appointment.setPatient(patient);
        appointment.setDentist(currentUser);
        appointment.setStatus(EntityStatus.ACTIVE);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDetailedResponse response = buildResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment created successfully");
    }

    @Override
    public DataResult<AppointmentDetailedResponse> getById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        List<XrayImage> images = xrayImageRepository.findAllByAppointmentId(id);
        List<XrayImageResponse> xrayImages = images.stream()
                .map(xrayImageMapper::toResponse)
                .toList();
        AppointmentDetailedResponse response = buildResponse(appointment);
        response.setXrayImages(xrayImages);
        return new SuccessDataResult<>(response, "Appointment found successfully");
    }

    @Override
    public DataResult<PageData<AppointmentResponse>> getAllByPatientId(UUID patientId, EntityStatus status, int page, int size) {
        Patient patient = patientService.getPatientById(patientId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Appointment> appointmentPage = appointmentRepository.findAllByPatientIdAndStatus(patient.getId(), status, pageable);
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

    @Override
    public DataResult<AppointmentDetailedResponse> updateById(UUID id, AppointmentUpdateRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidInputException("End time must be after start time", ErrorCode.INVALID_APPOINTMENT_TIME);
        }
        if (request.getPaidAmount().compareTo(request.getPrice()) > 0) {
            throw new InvalidInputException("Paid amount cannot be greater than price", ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        validateDentistAvailabilityForUpdate(id, existingAppointment.getDentist().getId(), request);
        validatePatientAvailabilityForUpdate(id, existingAppointment.getPatient().getId(), request);
        Appointment updatedAppointment = appointmentMapper.updateRequestToEntity(request, existingAppointment);
        Appointment savedAppointment = appointmentRepository.save(updatedAppointment);
        AppointmentDetailedResponse response = buildResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment updated successfully");
    }

    @Override
    public DataResult<AppointmentDetailedResponse> cancelById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        appointment.setStatus(EntityStatus.DELETED);
        appointment.setDeletedAt(LocalDateTime.now());
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDetailedResponse response = buildResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment canceled successfully");
    }

    @Override
    public DataResult<AppointmentDetailedResponse> activateById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        if (appointment.getStatus() == EntityStatus.ACTIVE) {
            throw new InvalidInputException("Appointment is already active", ErrorCode.APPOINTMENT_CONFLICT);
        }
        if (appointment.getPatient().getStatus() != EntityStatus.ACTIVE) {
            throw new InvalidInputException("Patient is deleted. You cannot activate this appointment", ErrorCode.PATIENT_NOT_ACTIVE);
        }
        appointment.setStatus(EntityStatus.ACTIVE);
        appointment.setDeletedAt(null);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDetailedResponse response = buildResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment activated successfully");
    }

    @Override
    public DataResult<AppointmentTodayCountResponse> getTodayAppointmentCount() {
        Long count = appointmentRepository.countByDate(LocalDate.now());
        AppointmentTodayCountResponse response = new AppointmentTodayCountResponse(count);
        return new SuccessDataResult<>(response, "Appointment today count successfully");
    }

    private void validateDentistAvailabilityForCreate(UUID dentistId, AppointmentCreateRequest request) {
        boolean hasConflict = appointmentRepository.existsByDentistIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                dentistId,
                request.getDate(),
                request.getEndTime(),
                request.getStartTime()
        );
        if (hasConflict) {
            throw new ConflictException("Dentist already has another appointment during this time", ErrorCode.APPOINTMENT_CONFLICT);
        }
    }

    private void validatePatientAvailabilityForCreate(UUID patientId, AppointmentCreateRequest request) {
        boolean hasConflict = appointmentRepository.existsByPatientIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
                patientId,
                request.getDate(),
                request.getEndTime(),
                request.getStartTime()
        );
        if (hasConflict) {
            throw new ConflictException("Patient already has another appointment during this time", ErrorCode.APPOINTMENT_CONFLICT);
        }
    }

    private void validateDentistAvailabilityForUpdate(UUID appointmentId, UUID dentistId, AppointmentUpdateRequest request) {
        boolean hasConflict = appointmentRepository.existsByDentistIdAndDateAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                dentistId,
                request.getDate(),
                appointmentId,
                request.getEndTime(),
                request.getStartTime()
        );
        if (hasConflict) {
            throw new ConflictException("Dentist already has another appointment during this time", ErrorCode.APPOINTMENT_CONFLICT);
        }
    }

    private void validatePatientAvailabilityForUpdate(UUID appointmentId, UUID patientId, AppointmentUpdateRequest request) {
        boolean hasConflict = appointmentRepository.existsByPatientIdAndDateAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                patientId,
                request.getDate(),
                appointmentId,
                request.getEndTime(),
                request.getStartTime()
        );
        if (hasConflict) {
            throw new ConflictException("Patient already has another appointment during this time", ErrorCode.APPOINTMENT_CONFLICT);
        }
    }

    private AppointmentDetailedResponse buildResponse(Appointment appointment) {
        User currentUser = userService.getCurrentUser();
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(appointment);
        if (currentUser.getRole() != Role.ADMIN) {
            response.setPrice(null);
            response.setPaidAmount(null);
            response.setRemainingAmount(null);
        }
        return response;
    }
}

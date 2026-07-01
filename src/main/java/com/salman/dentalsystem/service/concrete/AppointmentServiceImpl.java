package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.InvalidAppointmentTimeException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.AppointmentMapper;
import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Doctor;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
import com.salman.dentalsystem.service.abstraction.DoctorService;
import com.salman.dentalsystem.service.abstraction.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    public DataResult<AppointmentDetailedResponse> createAppointment(AppointmentCreateRequest request) {
        validateAppointmentTime(request.getStartTime(), request.getEndTime());
        Patient patient = patientService.getEntity(request.getPatientId());
        Doctor doctor = doctorService.getEntity(request.getDoctorId());
        Appointment appointment = appointmentMapper.createRequestToEntity(request);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus(EntityStatus.ACTIVE);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment created successfully");
    }

    @Override
    public DataResult<PageData<AppointmentResponse>> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Appointment> appointmentPage = appointmentRepository.findAllByStatusNot(EntityStatus.DELETED, pageable);
        PageData<AppointmentResponse> pageData = PageData.<AppointmentResponse>builder()
                .totalPages(appointmentPage.getTotalPages())
                .totalElements(appointmentPage.getTotalElements())
                .firstPage(appointmentPage.isFirst())
                .lastPage(appointmentPage.isLast())
                .page(appointmentPage.getNumber())
                .size(appointmentPage.getSize())
                .content(appointmentPage.getContent().stream().map(appointmentMapper::toResponse).toList())
                .build();
        return new SuccessDataResult<>(pageData, "Appointments found successfully");
    }

    @Override
    public DataResult<AppointmentDetailedResponse> getById(UUID id) {
        Appointment appointment = appointmentRepository.findByIdAndStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(appointment);
        return new SuccessDataResult<>(response, "Appointment found successfully");
    }

    @Override
    public DataResult<AppointmentDetailedResponse> updateById(UUID id, AppointmentUpdateRequest request) {
        validateAppointmentTime(request.getStartTime(), request.getEndTime());
        Appointment existingAppointment = appointmentRepository.findByIdAndStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        Appointment updatedAppointment = appointmentMapper.updateRequestToEntity(request, existingAppointment);
        if (!existingAppointment.getDoctor().getId().equals(request.getDoctorId())) {
            Doctor doctor = doctorService.getEntity(request.getDoctorId());
            updatedAppointment.setDoctor(doctor);
        }
        if (!existingAppointment.getPatient().getId().equals(request.getPatientId())) {
            Patient patient = patientService.getEntity(request.getPatientId());
            updatedAppointment.setPatient(patient);
        }
        Appointment savedAppointment = appointmentRepository.save(updatedAppointment);
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment updated successfully");
    }

    private void validateAppointmentTime(LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new InvalidAppointmentTimeException("End time must be after start time.", ErrorCode.INVALID_APPOINTMENT_TIME);
        }
    }

    @Override
    public DataResult<AppointmentDetailedResponse> deleteById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id, ErrorCode.APPOINTMENT_NOT_FOUND));
        appointment.setStatus(EntityStatus.DELETED);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        AppointmentDetailedResponse response = appointmentMapper.toDetailedResponse(savedAppointment);
        return new SuccessDataResult<>(response, "Appointment deleted successfully");
    }
}

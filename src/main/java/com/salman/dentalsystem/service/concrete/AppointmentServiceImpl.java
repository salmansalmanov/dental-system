package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.mapper.AppointmentMapper;
import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Doctor;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
import com.salman.dentalsystem.service.abstraction.DoctorService;
import com.salman.dentalsystem.service.abstraction.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    public DataResult<AppointmentDetailedResponse> createAppointment(AppointmentCreateRequest request) {
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
}

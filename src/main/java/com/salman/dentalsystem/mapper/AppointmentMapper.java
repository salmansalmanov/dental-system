package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Patient;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PatientMapper.class, DoctorMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "status", constant = "PENDING")
    Appointment createRequestToEntity(AppointmentCreateRequest request);

    AppointmentDetailedResponse toDetailedResponse(Appointment appointment);

    @AfterMapping
    default void calculateUnpaidAmount(Appointment appointment, @MappingTarget AppointmentDetailedResponse response) {
        response.setUnpaidAmount(appointment.getPrice().subtract(appointment.getPaidAmount()));
    }

    @Mapping(target = "patientFullName", source = "patient", qualifiedByName = "generateFullName")
    AppointmentResponse toResponse(Appointment appointment);

    @Named("generateFullName")
    default String generateFullName(Patient patient) {
        return patient.getName() + " " + patient.getSurname();
    }

    Appointment updateRequestToEntity(AppointmentUpdateRequest request, @MappingTarget Appointment appointment);
}

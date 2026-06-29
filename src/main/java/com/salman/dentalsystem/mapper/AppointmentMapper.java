package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PatientMapper.class, DoctorMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "status", constant = "PENDING")
    Appointment createRequestToEntity(AppointmentCreateRequest request);

    AppointmentDetailedResponse toDetailedResponse(Appointment appointment);
}

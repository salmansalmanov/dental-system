package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AppointmentMapper {

    @Mapping(target = "status", constant = "PENDING")
    Appointment createRequestToEntity(AppointmentCreateRequest request);

    AppointmentDetailedResponse toDetailedResponse(Appointment appointment);

    @AfterMapping
    default void calculateRemainingAmount(@MappingTarget AppointmentDetailedResponse response) {
        response.setRemainingAmount(response.getPrice().subtract(response.getPaidAmount()));
    }

    AppointmentResponse toResponse(Appointment appointment);

    Appointment updateRequestToEntity(AppointmentUpdateRequest request, @MappingTarget Appointment appointment);
}

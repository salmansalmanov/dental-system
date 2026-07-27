package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.request.PatientUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "status", constant = "PENDING")
    Patient createRequestToEntity(PatientCreateRequest request);

    PatientDetailedResponse toDetailedResponse(Patient patient);

    PatientResponse toResponse(Patient patient);

    Patient updateRequestToEntity(PatientUpdateRequest request, @MappingTarget Patient entity);
}

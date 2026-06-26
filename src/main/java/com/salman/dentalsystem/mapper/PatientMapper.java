package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "role", constant = "PATIENT")
    Patient toEntity(PatientCreateRequest request);

    PatientDetailedResponse toDetailedResponse(Patient patient);
}

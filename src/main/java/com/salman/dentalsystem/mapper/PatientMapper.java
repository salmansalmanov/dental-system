package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "role", constant = "PATIENT")
    @Mapping(target = "pin", source = "pin", qualifiedByName = "toUpper")
    Patient toEntity(PatientCreateRequest request);

    PatientDetailedResponse toDetailedResponse(Patient patient);

    @Named("toUpper")
    default String toUpper(String value) {
        return value.toUpperCase();
    }

    PatientResponse toResponse(Patient patient);
}

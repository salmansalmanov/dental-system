package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.request.DoctorUpdateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.model.dto.response.DoctorResponse;
import com.salman.dentalsystem.model.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "role", constant = "DOCTOR")
    @Mapping(target = "pin", source = "pin", qualifiedByName = "toUpper")
    @Mapping(target = "status", constant = "PENDING")
    Doctor createRequestToEntity(DoctorCreateRequest request);

    @Named("toUpper")
    default String toUpper(String value) {
        return value.toUpperCase();
    }

    DoctorDetailedResponse toDetailedResponse(Doctor doctor);

    DoctorResponse toResponse(Doctor doctor);

    Doctor updateRequestToEntity(DoctorUpdateRequest request, @MappingTarget Doctor doctor);
}

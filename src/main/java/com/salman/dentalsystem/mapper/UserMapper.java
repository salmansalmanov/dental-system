package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.request.UserCreateRequest;
import com.salman.dentalsystem.model.dto.request.UserUpdateRequest;
import com.salman.dentalsystem.model.dto.response.UserCreateResponse;
import com.salman.dentalsystem.model.dto.response.UserDetailedResponse;
import com.salman.dentalsystem.model.dto.response.UserResponse;
import com.salman.dentalsystem.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "status", constant = "PENDING")
    User createRequestToEntity(UserCreateRequest request);

    UserDetailedResponse toDetailedResponse(User user);

    @Named("toUpper")
    default String toUpper(String value) {
        return value.toUpperCase();
    }

    UserResponse toResponse(User user);

    User updateRequestToEntity(UserUpdateRequest request, @MappingTarget User entity);

    UserCreateResponse toCreateResponse(User user);
}

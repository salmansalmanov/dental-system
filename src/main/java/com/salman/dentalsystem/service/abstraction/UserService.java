package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.request.UserCreateRequest;
import com.salman.dentalsystem.model.dto.request.UserUpdateRequest;
import com.salman.dentalsystem.model.dto.response.UserCreateResponse;
import com.salman.dentalsystem.model.dto.response.UserDetailedResponse;
import com.salman.dentalsystem.model.dto.response.UserResponse;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;

import java.util.UUID;

public interface UserService {
    DataResult<UserCreateResponse> create(UserCreateRequest request);

    DataResult<UserDetailedResponse> getById(UUID id);

    DataResult<PageData<UserResponse>> getAll(String search, EntityStatus status, Role role, int page, int size);

    DataResult<UserDetailedResponse> updateById(UUID id, UserUpdateRequest request);

    DataResult<UserDetailedResponse> deleteById(UUID id);

    DataResult<UserDetailedResponse> activateById(UUID id);

    User getDentistById(UUID id);
}

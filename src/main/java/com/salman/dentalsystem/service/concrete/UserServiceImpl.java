package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.InvalidInputException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.UserMapper;
import com.salman.dentalsystem.model.dto.request.UserCreateRequest;
import com.salman.dentalsystem.model.dto.request.UserUpdateRequest;
import com.salman.dentalsystem.model.dto.response.UserDetailedResponse;
import com.salman.dentalsystem.model.dto.response.UserResponse;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.repository.UserRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public DataResult<UserDetailedResponse> create(UserCreateRequest request) {
        User user = userMapper.createRequestToEntity(request);
        user.setStatus(EntityStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        UserDetailedResponse userDetailedResponse = userMapper.toDetailedResponse(savedUser);
        return new SuccessDataResult<>(userDetailedResponse, "User created successfully");
    }

    @Override
    public DataResult<UserDetailedResponse> getById(UUID id) {
        User foundUser = userRepository.findByIdAndStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        UserDetailedResponse userDetailedResponse = userMapper.toDetailedResponse(foundUser);
        return new SuccessDataResult<>(userDetailedResponse, "User found successfully");
    }

    @Override
    public DataResult<PageData<UserResponse>> getAll(String search, Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAllFiltered(search, role, EntityStatus.DELETED, pageable);
        PageData<UserResponse> pageData = PageData.<UserResponse>builder()
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .firstPage(userPage.isFirst())
                .lastPage(userPage.isLast())
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .content(userPage.getContent().stream().map(userMapper::toResponse).toList())
                .build();
        return new SuccessDataResult<>(pageData, "Users found successfully");
    }

    @Override
    public DataResult<UserDetailedResponse> updateById(UUID id, UserUpdateRequest request) {
        User existingUser = userRepository.findByIdAndStatusNot(id, EntityStatus.DELETED)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        User updatedUser = userMapper.updateRequestToEntity(request, existingUser);
        User savedUser = userRepository.save(updatedUser);
        return new SuccessDataResult<>(userMapper.toDetailedResponse(savedUser), "User updated successfully");
    }

    @Override
    public DataResult<UserDetailedResponse> deleteById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        user.setStatus(EntityStatus.DELETED);
        User savedUser = userRepository.save(user);
        return new SuccessDataResult<>(userMapper.toDetailedResponse(savedUser), "User deleted successfully");
    }

    @Override
    public DataResult<UserDetailedResponse> activateById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        if (user.getStatus() == EntityStatus.ACTIVE) {
            throw new InvalidInputException("User is already active", ErrorCode.USER_ALREADY_ACTIVE);
        }
        user.setStatus(EntityStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        return new SuccessDataResult<>(userMapper.toDetailedResponse(savedUser), "User activated successfully");
    }
}

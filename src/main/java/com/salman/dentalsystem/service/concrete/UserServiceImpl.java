package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.exception.custom.ConflictException;
import com.salman.dentalsystem.exception.custom.InvalidInputException;
import com.salman.dentalsystem.exception.custom.NotFoundException;
import com.salman.dentalsystem.mapper.UserMapper;
import com.salman.dentalsystem.model.dto.request.UserCreateRequest;
import com.salman.dentalsystem.model.dto.request.UserPasswordChangeRequest;
import com.salman.dentalsystem.model.dto.request.UserUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PasswordResetResponse;
import com.salman.dentalsystem.model.dto.response.UserCreateResponse;
import com.salman.dentalsystem.model.dto.response.UserDetailedResponse;
import com.salman.dentalsystem.model.dto.response.UserResponse;
import com.salman.dentalsystem.model.entity.RefreshToken;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.repository.RefreshTokenRepository;
import com.salman.dentalsystem.repository.UserRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.admin.username}")
    private String adminUsername;

    @Override
    public DataResult<UserCreateResponse> create(UserCreateRequest request) {
        User user = userMapper.createRequestToEntity(request);
        user.setStatus(EntityStatus.ACTIVE);
        String username = user.getName().toLowerCase() + "." + user.getSurname().toLowerCase();
        if (userRepository.existsByUsername(username)) {
            for (int i = 2; i < Integer.MAX_VALUE; i++) {
                if (!userRepository.existsByUsername(username)) {
                    break;
                }
                username = user.getName().toLowerCase() + "." + user.getSurname().toLowerCase() + "." + i;
            }
        }
        String password = generatePassword();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        UserCreateResponse response = userMapper.toCreateResponse(savedUser);
        response.setPassword(password);
        return new SuccessDataResult<>(response, "User created successfully. This password will be displayed only once. Please save it in a secure place and share it with the user. You will not be able to view it again.");
    }

    @Override
    public DataResult<UserDetailedResponse> getById(UUID id) {
        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        UserDetailedResponse userDetailedResponse = userMapper.toDetailedResponse(foundUser);
        return new SuccessDataResult<>(userDetailedResponse, "User found successfully");
    }

    @Override
    public DataResult<PageData<UserResponse>> getAll(String search, EntityStatus status, Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAllFiltered(search, status, role, pageable);
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
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        if (adminUsername.equals(existingUser.getUsername())) {
            throw new InvalidInputException("This system user cannot be modified", ErrorCode.UNAUTHORIZED_ACTION);
        }
        User updatedUser = userMapper.updateRequestToEntity(request, existingUser);
        User savedUser = userRepository.save(updatedUser);
        return new SuccessDataResult<>(userMapper.toDetailedResponse(savedUser), "User updated successfully");
    }

    @Override
    @Transactional
    public DataResult<UserDetailedResponse> deleteById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        if (adminUsername.equals(user.getUsername())) {
            throw new InvalidInputException("This system user cannot be modified", ErrorCode.UNAUTHORIZED_ACTION);
        }
        User currentUser = getCurrentUser();
        if (user.getId().equals(currentUser.getId())) {
            throw new InvalidInputException("You cannot delete yourself", ErrorCode.UNAUTHORIZED_ACTION);
        }
        if (user.getRole() == Role.ADMIN) {
            throw new InvalidInputException("Admin users cannot be deleted", ErrorCode.UNAUTHORIZED_ACTION);
        }
        user.setStatus(EntityStatus.DELETED);
        user.setDeletedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        refreshTokenRepository.findByUser(savedUser)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
        return new SuccessDataResult<>(userMapper.toDetailedResponse(savedUser), "User deleted successfully");
    }

    @Override
    public DataResult<UserDetailedResponse> activateById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        if (adminUsername.equals(user.getUsername())) {
            throw new InvalidInputException("This system user cannot be modified", ErrorCode.UNAUTHORIZED_ACTION);
        }
        if (user.getStatus() == EntityStatus.ACTIVE) {
            throw new ConflictException("User is already active", ErrorCode.USER_ALREADY_ACTIVE);
        }
        user.setStatus(EntityStatus.ACTIVE);
        user.setDeletedAt(null);
        User savedUser = userRepository.save(user);
        return new SuccessDataResult<>(userMapper.toDetailedResponse(savedUser), "User activated successfully");
    }

    @Override
    public User getDentistById(UUID id) {
        return userRepository.findByIdAndRoleAndStatus(id, Role.DENTIST, EntityStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public DataResult<UserDetailedResponse> getMyProfile() {
        User user = getCurrentUser();
        UserDetailedResponse response = userMapper.toDetailedResponse(user);
        return new SuccessDataResult<>(response, "My profile retrieved successfully");
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Current user not found", ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public DataResult<UserDetailedResponse> updateMyProfile(UserUpdateRequest request) {
        User existingUser = getCurrentUser();
        User updatedUser = userMapper.updateRequestToEntity(request, existingUser);
        User savedUser = userRepository.save(updatedUser);
        UserDetailedResponse response = userMapper.toDetailedResponse(savedUser);
        return new SuccessDataResult<>(response, "My profile updated successfully");
    }

    @Override
    public DataResult<UserDetailedResponse> changeMyPassword(UserPasswordChangeRequest request) {
        User currentUser = getCurrentUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new InvalidInputException("Current password is incorrect", ErrorCode.INVALID_PASSWORD);
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        RefreshToken refreshToken = refreshTokenRepository.findByUser(currentUser)
                .orElseThrow(() -> new NotFoundException("Refresh token not found for user: " + currentUser.getId(), ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        User savedUser = userRepository.save(currentUser);
        UserDetailedResponse response = userMapper.toDetailedResponse(savedUser);
        return new SuccessDataResult<>(response, "Password changed successfully");
    }

    @Override
    public DataResult<PasswordResetResponse> resetPassword(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id, ErrorCode.USER_NOT_FOUND));
        if (adminUsername.equals(user.getUsername())) {
            throw new InvalidInputException("This system user cannot be modified", ErrorCode.UNAUTHORIZED_ACTION);
        }
        String newPassword = generatePassword();
        user.setPassword(passwordEncoder.encode(newPassword));
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Refresh token not found for user: " + user.getId(), ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        User savedUser = userRepository.save(user);
        PasswordResetResponse response = new PasswordResetResponse(savedUser.getUsername(), newPassword);
        return new SuccessDataResult<>(response, "Password reset successfully. This password will be displayed only once. Please save it in a secure place and share it with the user. You will not be able to view it again.");
    }

    private String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomIndex = RANDOM.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }
        return password.toString();
    }
}

package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.UserCreateRequest;
import com.salman.dentalsystem.model.dto.request.UserUpdateRequest;
import com.salman.dentalsystem.model.dto.response.UserCreateResponse;
import com.salman.dentalsystem.model.dto.response.UserDetailedResponse;
import com.salman.dentalsystem.model.dto.response.UserResponse;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.service.abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<DataResult<UserCreateResponse>> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<UserDetailedResponse>> getUserById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getById(id));
    }

    @GetMapping
    public ResponseEntity<DataResult<PageData<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAll(search, status, role, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<UserDetailedResponse>> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateById(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResult<UserDetailedResponse>> deleteUser(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.deleteById(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<DataResult<UserDetailedResponse>> activateUser(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.activateById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<DataResult<UserDetailedResponse>> getMyProfile() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<DataResult<UserDetailedResponse>> updateMyProfile(@RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateMyProfile(request));
    }
}

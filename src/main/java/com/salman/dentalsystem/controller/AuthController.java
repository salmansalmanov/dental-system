package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.LoginRequest;
import com.salman.dentalsystem.model.dto.request.RefreshRequest;
import com.salman.dentalsystem.model.dto.response.LoginResponse;
import com.salman.dentalsystem.model.dto.response.RefreshResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.service.abstraction.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<DataResult<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<DataResult<RefreshResponse>> refresh(@RequestBody @Valid RefreshRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.refresh(request));
    }
}

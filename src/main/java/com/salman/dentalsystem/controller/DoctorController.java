package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.service.abstraction.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DataResult<DoctorDetailedResponse>> create(@RequestBody @Valid DoctorCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doctorService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<DoctorDetailedResponse>> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(doctorService.getById(id));
    }
}

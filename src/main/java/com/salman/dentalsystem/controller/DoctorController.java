package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.DoctorCreateRequest;
import com.salman.dentalsystem.model.dto.request.DoctorUpdateRequest;
import com.salman.dentalsystem.model.dto.response.DoctorDetailedResponse;
import com.salman.dentalsystem.model.dto.response.DoctorResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
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

    @GetMapping
    public ResponseEntity<DataResult<PageData<DoctorResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(doctorService.getAll(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<DoctorDetailedResponse>> updateDoctorById(
            @PathVariable UUID id,
            @RequestBody @Valid DoctorUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(doctorService.updateById(id, request));
    }
}

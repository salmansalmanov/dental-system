package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.service.abstraction.PatientService;
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
@RequestMapping("/v1/patients")
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<DataResult<PatientDetailedResponse>> createPatient(@RequestBody @Valid PatientCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(patientService.create(request));
    }
}

package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.PatientCreateRequest;
import com.salman.dentalsystem.model.dto.request.PatientUpdateRequest;
import com.salman.dentalsystem.model.dto.response.PatientDetailedResponse;
import com.salman.dentalsystem.model.dto.response.PatientResponse;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.service.abstraction.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/deleted")
    public ResponseEntity<DataResult<PageData<PatientResponse>>> getAllDeletedPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientService.getAllDeleted(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<PatientDetailedResponse>> getPatientById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientService.getById(id));
    }

    @GetMapping
    public ResponseEntity<DataResult<PageData<PatientResponse>>> getAllPatients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientService.getAll(search, status, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<PatientDetailedResponse>> updatePatient(
            @PathVariable UUID id,
            @RequestBody @Valid PatientUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResult<PatientDetailedResponse>> deletePatient(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientService.deleteById(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<DataResult<PatientDetailedResponse>> activatePatient(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(patientService.activateById(id));
    }
}

package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> createAppointment(
            @RequestBody @Valid AppointmentCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.createAppointment(request));
    }

    @GetMapping
    public ResponseEntity<DataResult<PageData<AppointmentResponse>>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> updateAppointment(
            @PathVariable UUID id,
            @RequestBody @Valid AppointmentUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.updateById(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> deleteAppointment(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.deleteById(id));
    }
}

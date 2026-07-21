package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.request.AppointmentUpdateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentTodayCountResponse;
import com.salman.dentalsystem.model.dto.response.AppointmentTodayResponse;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.PageData;
import com.salman.dentalsystem.result.Result;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/patients/{patientId}/appointments")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> createAppointment(
            @PathVariable UUID patientId,
            @RequestBody @Valid AppointmentCreateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.create(patientId, request));
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getById(id));
    }

    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<DataResult<PageData<AppointmentResponse>>> getAllAppointmentsByPatientId(
            @PathVariable UUID patientId,
            @RequestParam(required = false) EntityStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getAllByPatientId(patientId, status, page, size));
    }

    @GetMapping("/appointments/today")
    public ResponseEntity<DataResult<PageData<AppointmentTodayResponse>>> getAllTodayAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getAllForToday(page, size));
    }

    @GetMapping("/appointments")
    public ResponseEntity<DataResult<AppointmentTodayCountResponse>> getTodayAppointmentCount() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getTodayAppointmentCount());
    }

    @PutMapping("/appointments/{id}")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> updateAppointmentById(
            @PathVariable UUID id,
            @RequestBody @Valid AppointmentUpdateRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.updateById(id, request));
    }

    @PatchMapping("/appointments/{id}/cancel")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> cancelAppointmentById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.cancelById(id));
    }

    @PatchMapping("/appointments/{id}/activate")
    public ResponseEntity<DataResult<AppointmentDetailedResponse>> activateAppointmentById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.activateById(id));
    }

    @GetMapping("/appointments/trash")
    public ResponseEntity<DataResult<PageData<AppointmentResponse>>> getAllTrashedAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.getAllTrashed(page, size));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Result> deleteAppointmentById(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.deleteById(id));
    }

    @DeleteMapping("appointments/empty-trash")
    public ResponseEntity<Result> deleteAllTrashedAppointments() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appointmentService.deleteAllTrashed());
    }
}

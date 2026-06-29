package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.request.AppointmentCreateRequest;
import com.salman.dentalsystem.model.dto.response.AppointmentDetailedResponse;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.service.abstraction.AppointmentService;
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
}

package com.salman.dentalsystem.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String treatment;
    private UUID dentistId;
}

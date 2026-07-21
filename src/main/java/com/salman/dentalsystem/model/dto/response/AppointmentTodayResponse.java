package com.salman.dentalsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentTodayResponse {
    private UUID id;
    private String patientFullName;
    private String dentistFullName;
    private LocalTime startTime;
    private LocalTime endTime;
}

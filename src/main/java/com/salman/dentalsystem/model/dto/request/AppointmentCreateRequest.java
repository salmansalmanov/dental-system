package com.salman.dentalsystem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateRequest {

    @NotNull(message = "Patient ID cannot be null")
    private UUID patientId;

    @NotNull(message = "Doctor ID cannot be null")
    private UUID doctorId;

    @NotBlank(message = "Treatment cannot be blank")
    @Size(max = 250, message = "Treatment must be between 1 and 250 symbols")
    private String treatment;

    @NotNull(message = "Appointment date cannot be null")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;

    @NotNull(message = "Price cannot be null")
    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    private BigDecimal price;
}

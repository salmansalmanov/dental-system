package com.salman.dentalsystem.model.dto.response;

import com.salman.dentalsystem.model.enums.EntityStatus;
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
public class AppointmentDetailedResponse {
    private UUID id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private UserResponse dentist;
    private String treatment;
    private EntityStatus status;
    private BigDecimal price;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
}

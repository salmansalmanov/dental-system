package com.salman.dentalsystem.model.dto.response;

import com.salman.dentalsystem.model.enums.Role;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DentistReportResponse {
    private UUID id;
    private String fullName;
    private Role role;
    private Long totalAppointments;
    private BigDecimal totalPrice;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalRemaining;
}

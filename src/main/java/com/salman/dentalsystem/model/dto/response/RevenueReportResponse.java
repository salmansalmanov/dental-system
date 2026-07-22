package com.salman.dentalsystem.model.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportResponse {
    private BigDecimal totalPrice;
    private BigDecimal totalPaidAmount;
    private BigDecimal totalRemaining;
    private Long totalAppointments;
    private LocalDate startDate;
    private LocalDate endDate;
    List<DentistReportResponse> dentistReports;
    List<PatientReportResponse> patientReports;
}

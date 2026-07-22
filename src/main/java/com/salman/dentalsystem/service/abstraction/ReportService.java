package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.response.RevenueReportResponse;
import com.salman.dentalsystem.result.DataResult;

import java.time.LocalDate;

public interface ReportService {
    DataResult<RevenueReportResponse> getRevenueReport(LocalDate startDate, LocalDate endDate, Integer year, Integer month);
}

package com.salman.dentalsystem.service.concrete;

import com.salman.dentalsystem.model.enums.AppointmentStatus;
import com.salman.dentalsystem.model.dto.response.DentistReportResponse;
import com.salman.dentalsystem.model.dto.response.PatientReportResponse;
import com.salman.dentalsystem.model.dto.response.RevenueReportResponse;
import com.salman.dentalsystem.model.entity.Appointment;
import com.salman.dentalsystem.model.entity.Patient;
import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.repository.AppointmentRepository;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.result.SuccessDataResult;
import com.salman.dentalsystem.service.abstraction.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public DataResult<RevenueReportResponse> getRevenueReport(LocalDate startDate, LocalDate endDate, Integer year, Integer month) {
        LocalDate fromDate;
        LocalDate toDate;

        if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            fromDate = yearMonth.atDay(1);
            toDate = yearMonth.atEndOfMonth();
        } else {
            fromDate = (startDate != null) ? startDate : LocalDate.of(2000, 1, 1);
            toDate = (endDate != null) ? endDate : LocalDate.of(2099, 12, 31);
        }

        List<Appointment> rawAppointments = appointmentRepository.findAllAppointmentsBetween(fromDate, toDate);
        if (rawAppointments == null) {
            rawAppointments = Collections.emptyList();
        }

        List<Appointment> appointments = rawAppointments.stream()
                .filter(a -> a.getAppointmentStatus() != null)
                .filter(a -> a.getAppointmentStatus() != AppointmentStatus.DELETED
                        && a.getAppointmentStatus() != AppointmentStatus.TRASH)
                .collect(Collectors.toList());

        BigDecimal totalPrice = calculateSum(appointments, Appointment::getPrice);
        BigDecimal totalPaid = calculateSum(appointments, Appointment::getPaidAmount);
        BigDecimal totalRemaining = totalPrice.subtract(totalPaid);

        List<DentistReportResponse> dentistReports = buildDentistReports(appointments);

        List<PatientReportResponse> patientReports = buildPatientReports(appointments);

        RevenueReportResponse response = RevenueReportResponse.builder()
                .totalPrice(totalPrice)
                .totalPaidAmount(totalPaid)
                .totalRemaining(totalRemaining)
                .totalAppointments((long) appointments.size())
                .startDate((startDate != null || (year != null && month != null)) ? fromDate : null)
                .endDate((endDate != null || (year != null && month != null)) ? toDate : null)
                .dentistReports(dentistReports)
                .patientReports(patientReports)
                .build();

        return new SuccessDataResult<>(response, "Revenue report prepared successfully");
    }

    // --- KÖMƏKÇİ REFACTOR METODLARI ---

    private BigDecimal calculateSum(List<Appointment> appointments, Function<Appointment, BigDecimal> mapper) {
        return appointments.stream()
                .map(mapper)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatFullName(String name, String surname) {
        return ((name != null ? name : "") + " " + (surname != null ? surname : "")).trim();
    }

    private List<DentistReportResponse> buildDentistReports(List<Appointment> appointments) {
        Map<User, List<Appointment>> appointmentsByDentist = appointments.stream()
                .filter(a -> a.getDentist() != null)
                .collect(Collectors.groupingBy(Appointment::getDentist));

        return appointmentsByDentist.entrySet().stream()
                .map(entry -> {
                    User dentist = entry.getKey();
                    List<Appointment> dentistApps = entry.getValue();

                    BigDecimal price = calculateSum(dentistApps, Appointment::getPrice);
                    BigDecimal paid = calculateSum(dentistApps, Appointment::getPaidAmount);

                    return DentistReportResponse.builder()
                            .id(dentist.getId())
                            .fullName(formatFullName(dentist.getName(), dentist.getSurname()))
                            .role(dentist.getRole())
                            .totalAppointments((long) dentistApps.size())
                            .totalPrice(price)
                            .totalPaidAmount(paid)
                            .totalRemaining(price.subtract(paid))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<PatientReportResponse> buildPatientReports(List<Appointment> appointments) {
        Map<Patient, List<Appointment>> appointmentsByPatient = appointments.stream()
                .filter(a -> a.getPatient() != null)
                .collect(Collectors.groupingBy(Appointment::getPatient));

        return appointmentsByPatient.entrySet().stream()
                .map(entry -> {
                    Patient patient = entry.getKey();
                    List<Appointment> patientApps = entry.getValue();

                    BigDecimal price = calculateSum(patientApps, Appointment::getPrice);
                    BigDecimal paid = calculateSum(patientApps, Appointment::getPaidAmount);

                    return PatientReportResponse.builder()
                            .id(patient.getId())
                            .fullName(formatFullName(patient.getName(), patient.getSurname()))
                            .phoneNumber(patient.getPhoneNumber())
                            .totalAppointments((long) patientApps.size())
                            .totalPrice(price)
                            .totalPaidAmount(paid)
                            .totalRemaining(price.subtract(paid))
                            .build();
                })
                .collect(Collectors.toList());
    }
}
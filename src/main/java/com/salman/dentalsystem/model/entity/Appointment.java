package com.salman.dentalsystem.model.entity;

import com.salman.dentalsystem.model.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointments")
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;
    private String treatment;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal price;
    private BigDecimal paidAmount = BigDecimal.ZERO;
}

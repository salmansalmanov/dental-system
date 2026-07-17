package com.salman.dentalsystem.model.entity;

import com.salman.dentalsystem.model.enums.XraySessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "xray_sessions")
public class XraySession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    private XraySessionStatus status;
    private LocalDateTime expiresAt;
}

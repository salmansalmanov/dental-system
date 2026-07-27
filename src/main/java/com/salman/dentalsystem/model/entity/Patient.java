package com.salman.dentalsystem.model.entity;

import com.salman.dentalsystem.model.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients")
public class Patient extends BaseEntity {
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private String pin;
    private String address;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;
    private LocalDateTime deletedAt;
}

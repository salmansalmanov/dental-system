package com.salman.dentalsystem.model.entity;

import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends BaseEntity {
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private String pin;

    @Enumerated(EnumType.STRING)
    private EntityStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;
}

package com.salman.dentalsystem.model.dto.response;

import com.salman.dentalsystem.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatientDetailedResponse {
    private UUID id;
    private String name;
    private String surname;
    private String patronymic;
    private String phoneNumber;
    private String pin;
    private Role role;
}

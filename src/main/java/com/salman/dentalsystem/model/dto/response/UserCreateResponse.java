package com.salman.dentalsystem.model.dto.response;

import com.salman.dentalsystem.model.enums.EntityStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateResponse {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String password;
    private String patronymic;
    private String phoneNumber;
    private EntityStatus status;
}

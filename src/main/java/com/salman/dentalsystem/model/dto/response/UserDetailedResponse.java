package com.salman.dentalsystem.model.dto.response;

import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.model.enums.XrayAgent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailedResponse {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String patronymic;
    private String phoneNumber;
    private EntityStatus status;
    private Role role;
    private XrayAgent xrayAgent;
}

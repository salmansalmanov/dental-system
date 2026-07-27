package com.salman.dentalsystem.model.dto.request;

import com.salman.dentalsystem.model.enums.XrayAgent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientUpdateRequest {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 symbols")
    private String name;

    @NotBlank(message = "Surname cannot be blank")
    @Size(min = 3, max = 20, message = "Surname must be between 3 and 20 symbols")
    private String surname;

    @NotBlank(message = "Patronymic cannot be blank")
    @Size(min = 3, max = 20, message = "Patronymic must be between 3 and 20 symbols")
    private String patronymic;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+994\\d{9}$",
            message = "Phone number must start with +994 and be 13 characters long")
    private String phoneNumber;

    @NotBlank(message = "Pin cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]{7}$", message = "Pin must be 7 characters long")
    private String pin;

    @NotBlank(message = "Address cannot be blank")
    private String address;
}

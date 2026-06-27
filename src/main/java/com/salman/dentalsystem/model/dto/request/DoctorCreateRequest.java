package com.salman.dentalsystem.model.dto.request;

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
public class DoctorCreateRequest {

    @NotBlank(message = "Ad boş ola bilməz")
    @Size(min = 3, max = 20, message = "Simvol sayı '3' ilə '20' arasında ola bilər")
    private String name;

    @NotBlank(message = "Soyad boş ola bilməz")
    @Size(min = 3, max = 20, message = "Simvol sayı '3' ilə '20' arasında ola bilər")
    private String surname;

    @NotBlank(message = "Ata adı boş ola bilməz")
    @Size(min = 3, max = 20, message = "Simvol sayı '3' ilə '20' arasında ola bilər")
    private String patronymic;

    @NotBlank(message = "Telefon nömrəsi boş ola bilməz")
    @Pattern(regexp = "^\\+994\\d{9}$",
            message = "Telefon nömrəsi +994 ilə başlamalı və 13 simvol uzunluğunda olmalıdır")
    private String phoneNumber;

    @NotBlank(message = "FİN kod boş ola bilməz")
    @Size(min = 7, max = 7, message = "Simvol sayı yalnız '7' ola bilər")
    private String pin;
}

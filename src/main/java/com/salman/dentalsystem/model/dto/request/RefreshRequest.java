package com.salman.dentalsystem.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshRequest {

    @NotNull(message = "Refresh token is required")
    private UUID refreshToken;
}

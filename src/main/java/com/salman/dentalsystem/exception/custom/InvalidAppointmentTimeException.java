package com.salman.dentalsystem.exception.custom;

import com.salman.dentalsystem.model.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidAppointmentTimeException extends RuntimeException {
    private final ErrorCode code;

    public InvalidAppointmentTimeException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
}

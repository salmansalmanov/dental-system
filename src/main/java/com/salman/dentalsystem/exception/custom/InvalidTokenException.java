package com.salman.dentalsystem.exception.custom;

import com.salman.dentalsystem.model.enums.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final ErrorCode code;

    public InvalidTokenException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
}

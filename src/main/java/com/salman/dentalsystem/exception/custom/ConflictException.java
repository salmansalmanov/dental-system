package com.salman.dentalsystem.exception.custom;

import com.salman.dentalsystem.model.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {
    private final ErrorCode code;

    public ConflictException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }
}

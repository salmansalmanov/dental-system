package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.ResponseStatus;
import lombok.Getter;

@Getter
public class ErrorResult extends Result {
    private final ErrorCode code;

    public ErrorResult(String message, ErrorCode code) {
        super(ResponseStatus.ERROR, message);
        this.code = code;
    }
}

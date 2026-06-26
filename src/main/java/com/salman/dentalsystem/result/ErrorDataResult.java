package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ErrorCode;
import com.salman.dentalsystem.model.enums.ResponseStatus;
import lombok.Getter;

@Getter
public class ErrorDataResult<T> extends DataResult<T> {
    private final ErrorCode code;

    public ErrorDataResult(T data, String message, ErrorCode code) {
        super(data, ResponseStatus.ERROR, message);
        this.code = code;
    }
}

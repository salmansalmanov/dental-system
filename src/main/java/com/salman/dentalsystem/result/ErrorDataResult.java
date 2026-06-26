package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ResponseStatus;

public class ErrorDataResult<T> extends DataResult<T> {
    public ErrorDataResult(T data, String message) {
        super(data, ResponseStatus.ERROR, message);
    }
}

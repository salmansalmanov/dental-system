package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ResponseStatus;

public class SuccessDataResult<T> extends DataResult<T> {
    public SuccessDataResult(T data, String message) {
        super(data, ResponseStatus.SUCCESS, message);
    }
}

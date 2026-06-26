package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ResponseStatus;

public class ErrorResult extends Result {
    public ErrorResult(String message) {
        super(ResponseStatus.ERROR, message);
    }
}

package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ResponseStatus;

public class SuccessResult extends Result {
    public SuccessResult(String message) {
        super(ResponseStatus.SUCCESS, message);
    }
}

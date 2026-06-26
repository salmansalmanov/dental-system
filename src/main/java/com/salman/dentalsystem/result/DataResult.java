package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ResponseStatus;
import lombok.Getter;

@Getter
public abstract class DataResult<T> extends Result {
    private final T data;

    public DataResult(T data, ResponseStatus responseStatus, String message) {
        super(responseStatus, message);
        this.data = data;
    }
}

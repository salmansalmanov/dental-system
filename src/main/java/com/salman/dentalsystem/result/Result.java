package com.salman.dentalsystem.result;

import com.salman.dentalsystem.model.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public abstract class Result {
    private ResponseStatus status;
    private String message;
}

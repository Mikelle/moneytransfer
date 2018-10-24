package com.revolut.moneytransfer.exception;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParamIsNotNumberException extends Exception {
    String param;

    @Override
    public String getMessage() {
        return String.format("Param %s is not a number", param);
    }
}

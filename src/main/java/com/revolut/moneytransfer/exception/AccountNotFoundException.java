package com.revolut.moneytransfer.exception;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountNotFoundException extends Exception {
    long id;

    @Override
    public String getMessage() {
        return String.format("Account with id = %d not found", id);
    }
}

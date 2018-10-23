package com.revolut.moneytransfer.exception;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Value
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotEnoughMoneyException extends Exception {
    long id;
    BigDecimal amount;

    @Override
    public String getMessage() {
        return String.format("Account with id = %d doesn't have enough money to transfer this amount = %s", id, amount.toString());
    }
}

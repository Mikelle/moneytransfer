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
    BigDecimal accountBalance;
    BigDecimal amount;

    @Override
    public String getMessage() {
        return String.format("Account with id = %d have only %s. This is not enough to transfer this amount = %s",
                id, accountBalance.toString(), amount.toString());
    }
}

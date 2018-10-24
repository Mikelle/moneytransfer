package com.revolut.moneytransfer.model;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequest {
    String fromAccountId;
    String toAccountId;
    BigDecimal amount;
}

package com.revolut.moneytransfer.model;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferResponse {
    Account fromAccount;
    Account toAccount;
}

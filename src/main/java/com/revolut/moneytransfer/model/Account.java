package com.revolut.moneytransfer.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    final long id;

    @Setter
    volatile BigDecimal balance;

    final transient Lock lock = new ReentrantLock();

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }
}

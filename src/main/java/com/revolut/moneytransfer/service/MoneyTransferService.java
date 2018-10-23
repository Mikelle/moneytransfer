package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.exception.NegativeAmountException;
import com.revolut.moneytransfer.exception.NotEnoughMoneyException;
import com.revolut.moneytransfer.exception.SameAccountException;
import com.revolut.moneytransfer.model.Account;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

public class MoneyTransferService {

    public void moneyTransfer(Account from, Account to, BigDecimal amount) throws SameAccountException, NotEnoughMoneyException, NegativeAmountException {
        if (from.getId() == to.getId())
            throw new SameAccountException();

        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException();

        Lock firstLock = from.getId() > to.getId() ? to.getLock() : from.getLock();
        Lock secondLock = from.getId() > to.getId() ? from.getLock() : to.getLock();

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                if (from.getBalance().compareTo(amount) < 0)
                    throw new NotEnoughMoneyException(from.getId(), amount);

                from.setBalance(from.getBalance().subtract(amount));
                to.setBalance(to.getBalance().add(amount));

            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
    }
}

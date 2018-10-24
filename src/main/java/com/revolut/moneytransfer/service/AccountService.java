package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.exception.AccountNotFoundException;
import com.revolut.moneytransfer.exception.ParamIsNotNumberException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class AccountService {

    private AccountRepository accountRepository;

    private final AtomicLong accountIncrementingId = new AtomicLong();

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findAccount(String id) throws AccountNotFoundException, ParamIsNotNumberException {
        long accountId;
        try {
            accountId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ParamIsNotNumberException(id);
        }
        Account account = accountRepository.findOneById(accountId);
        if (account == null)
            throw new AccountNotFoundException(accountId);
        return account;
    }

    public Account createAccount(BigDecimal balance) {
        Account account = new Account(accountIncrementingId.incrementAndGet(), balance);
        accountRepository.save(account);
        return account;
    }

    public Collection<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }
}

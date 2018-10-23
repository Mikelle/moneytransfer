package com.revolut.moneytransfer.service;

import com.revolut.moneytransfer.exception.AccountNotFoundException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.repository.AccountRepository;

import java.util.Collection;

public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account findAccount(Long id) throws AccountNotFoundException {
        Account account = accountRepository.findOneById(id);
        if (account == null)
            throw new AccountNotFoundException(id);
        return account;
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public Collection<Account> getAllAccounts() {
        return accountRepository.getAllAccounts();
    }
}

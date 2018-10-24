package com.revolut.moneytransfer.repository;

import com.revolut.moneytransfer.model.Account;

import java.util.Collection;

public interface AccountRepository {
    Account findOneById(Long id);

    void save(Account account);

    Collection<Account> getAllAccounts();
}

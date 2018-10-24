package com.revolut.moneytransfer.repository;

import com.revolut.moneytransfer.model.Account;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountRepositoryInMemory implements AccountRepository {

    private ConcurrentMap<Long, Account> accountsById = new ConcurrentHashMap<>();

    public AccountRepositoryInMemory() {
    }

    @Override
    public Account findOneById(Long id) {
        return accountsById.get(id);
    }

    @Override
    public void save(Account account) {
        accountsById.putIfAbsent(account.getId(), account);
    }

    @Override
    public Collection<Account> getAllAccounts() {
        return accountsById.values();
    }
}

package com.revolut.moneytransfer.repository;

import com.revolut.moneytransfer.model.Account;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AccountRepositoryInMemory implements AccountRepository {

    private ConcurrentMap<Long, Account> accountsById = new ConcurrentHashMap<>();

    public AccountRepositoryInMemory() {
        createAccounts();
    }

    private void createAccounts() {
        Account firstAccount = new Account(1L, new BigDecimal("1000"));
        accountsById.put(firstAccount.getId(), firstAccount);
        Account secondAccount = new Account(2L, new BigDecimal("1000"));
        accountsById.put(secondAccount.getId(), secondAccount);
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

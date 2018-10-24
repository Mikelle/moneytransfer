package repository;

import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.repository.AccountRepository;
import com.revolut.moneytransfer.repository.AccountRepositoryInMemory;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;


public class AccountRepositoryInMemoryTest {

    AccountRepository accountRepository = new AccountRepositoryInMemory();

    @Before
    public void setUp() {
        Account firstAccount = new Account(1L, new BigDecimal("1000"));
        accountRepository.save(firstAccount);
        Account secondAccount = new Account(2L, new BigDecimal("1000"));
        accountRepository.save(secondAccount);
    }

    @Test
    public void saveTest() {
        long accountId = 3L;
        BigDecimal balance = new BigDecimal("5000");
        Account account = new Account(accountId, balance);
        accountRepository.save(account);
        Account savedAccount = accountRepository.findOneById(accountId);
        assertNotNull(savedAccount);
        assertThat(savedAccount.getId(), is(accountId));
        assertThat(savedAccount.getBalance(), is(balance));
    }

    @Test
    public void saveExistedAccountTest() {
        long accountId = 2L;
        BigDecimal balance = new BigDecimal("6000");
        Account account = new Account(accountId, balance);
        accountRepository.save(account);
        Account oldAccount = accountRepository.findOneById(accountId);
        assertThat(oldAccount.getBalance(), is(not(balance)));
        assertThat(oldAccount.getId(), is(accountId));
    }
}

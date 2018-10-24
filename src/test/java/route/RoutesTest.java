package route;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.revolut.moneytransfer.constants.PathConstants;
import com.revolut.moneytransfer.model.TransferRequest;
import com.revolut.moneytransfer.repository.AccountRepositoryInMemory;
import com.revolut.moneytransfer.route.Routes;
import com.revolut.moneytransfer.service.AccountService;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RoutesTest {

    private static Gson gson = new Gson();

    public static class RoutesTestSparkApplication implements SparkApplication {
        @Override
        public void init() {
            AccountService accountService = new AccountService(new AccountRepositoryInMemory());
            accountService.createAccount(new BigDecimal("1000"));
            accountService.createAccount(new BigDecimal("1000"));
            accountService.createAccount(new BigDecimal("3000"));
            new Routes(accountService).initRoutes();
        }
    }

    @ClassRule
    public static SparkServer<RoutesTestSparkApplication> testServer = new SparkServer<>(RoutesTest.RoutesTestSparkApplication.class, 4567);

    @Test
    public void getAccountsTest() throws HttpClientException {
        GetMethod getAccounts = testServer.get(PathConstants.PATH_ACCOUNTS, false);
        HttpResponse httpResponse = testServer.execute(getAccounts);
        assertThat(httpResponse.code(), is(200));
        assertThat(new String(httpResponse.body()), is("[{\"id\":1,\"balance\":1000},{\"id\":2,\"balance\":1000},{\"id\":3,\"balance\":3000}]"));
    }

    @Test
    public void getAccountByIdSuccessfullyTest() throws HttpClientException {
        GetMethod getAccountById = testServer.get(PathConstants.PATH_ACCOUNTS + "/3", false);
        HttpResponse httpResponse = testServer.execute(getAccountById);
        assertThat(httpResponse.code(), is(200));
        assertThat(new String(httpResponse.body()), is("{\"id\":3,\"balance\":3000}"));
    }

    @Test
    public void getAccountByIdWrongIdTest() throws HttpClientException {
        GetMethod getAccountById = testServer.get(PathConstants.PATH_ACCOUNTS + "/s", false);
        HttpResponse httpResponse = testServer.execute(getAccountById);
        assertThat(httpResponse.code(), is(400));
        assertThat(new String(httpResponse.body()), is("Param s is not a number"));
    }

    @Test
    public void getAccountByNotExistedIdTest() throws HttpClientException {
        GetMethod getAccountById = testServer.get(PathConstants.PATH_ACCOUNTS + "/1512", false);
        HttpResponse httpResponse = testServer.execute(getAccountById);
        assertThat(httpResponse.code(), is(404));
        assertThat(new String(httpResponse.body()), is("Account with id = 1512 not found"));
    }

    @Test
    public void createAccountSuccessfullyTest() throws HttpClientException {
        PostMethod postMethod = testServer.post(PathConstants.PATH_ACCOUNTS, "{\"balance\":1000}", false);
        HttpResponse httpResponse = testServer.execute(postMethod);
        assertThat(httpResponse.code(), is(201));
        assertThat(new String(httpResponse.body()), is("{\"id\":4,\"balance\":1000}"));
    }

    @Test
    public void createAccountWrongJsonTest() throws HttpClientException {
        PostMethod postMethod = testServer.post(PathConstants.PATH_ACCOUNTS, "{\"balance\":10s00}", false);
        HttpResponse httpResponse = testServer.execute(postMethod);
        assertThat(httpResponse.code(), is(400));
        assertThat(new String(httpResponse.body()), is("Failed to parse Json"));
    }

    @Test
    public void transferMoneySuccessfullyTest() throws HttpClientException {
        TransferRequest transferRequest = new TransferRequest("1", "2", new BigDecimal("100"));

        PostMethod postMethod = testServer.post(PathConstants.PATH_TRANSFER, gson.toJson(transferRequest),false);
        HttpResponse httpResponse = testServer.execute(postMethod);
        assertThat(httpResponse.code(), is(200));
        assertThat(new String(httpResponse.body()), is("{\"fromAccount\":{\"id\":1,\"balance\":900},\"toAccount\":{\"id\":2,\"balance\":1100}}"));
    }

    @Test
    public void transferMoneySameAccountException() throws HttpClientException {
        TransferRequest transferRequest = new TransferRequest("1", "1", new BigDecimal("1000"));
        PostMethod postMethod = testServer.post(PathConstants.PATH_TRANSFER, gson.toJson(transferRequest), false);
        HttpResponse httpResponse = testServer.execute(postMethod);
        assertThat(httpResponse.code(), is(400));
        assertThat(new String(httpResponse.body()), is("Can't transfer money to same account"));
    }

    @Test
    public void transferMoneyNotEnoughMoneyException() throws HttpClientException {
        TransferRequest transferRequest = new TransferRequest("1", "2", new BigDecimal("1000000"));
        PostMethod postMethod = testServer.post(PathConstants.PATH_TRANSFER, gson.toJson(transferRequest), false);
        HttpResponse httpResponse = testServer.execute(postMethod);
        assertThat(httpResponse.code(), is(400));
        assertThat(new String(httpResponse.body()), is( "Account with id = 1 have only 1000. This is not enough to transfer this amount = 1000000"));
    }

    @Test
    public void transferMoneyNegativeAmountException() throws HttpClientException {
        TransferRequest transferRequest = new TransferRequest("1", "2", new BigDecimal("-1000"));
        PostMethod postMethod = testServer.post(PathConstants.PATH_TRANSFER, gson.toJson(transferRequest), false);
        HttpResponse httpResponse = testServer.execute(postMethod);
        assertThat(httpResponse.code(), is(400));
        assertThat(new String(httpResponse.body()), is("Amount is negative"));
    }
}

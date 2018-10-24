package com.revolut.moneytransfer.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.revolut.moneytransfer.constants.PathConstants;
import com.revolut.moneytransfer.exception.AccountNotFoundException;
import com.revolut.moneytransfer.exception.NegativeAmountException;
import com.revolut.moneytransfer.exception.NotEnoughMoneyException;
import com.revolut.moneytransfer.exception.ParamIsNotNumberException;
import com.revolut.moneytransfer.exception.SameAccountException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.TransferRequest;
import com.revolut.moneytransfer.model.TransferResponse;
import com.revolut.moneytransfer.repository.AccountRepositoryInMemory;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.MoneyTransferService;
import com.revolut.moneytransfer.util.JsonTransformer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import spark.Response;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.eclipse.jetty.http.MimeTypes.Type.APPLICATION_JSON;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Routes {
    AccountService accountService;
    MoneyTransferService moneyTransferService;
    JsonTransformer json;
    Gson gson;

    public Routes() {
        this.accountService = new AccountService(new AccountRepositoryInMemory());
        this.moneyTransferService = new MoneyTransferService();
        this.json = new JsonTransformer();
        this.gson = new Gson();
    }

    // for testing
    public Routes(AccountService accountService) {
        this.accountService = accountService;
        this.moneyTransferService = new MoneyTransferService();
        this.json = new JsonTransformer();
        this.gson = new Gson();
    }

    public void initRoutes() {
        before((req, res) -> res.type(APPLICATION_JSON.asString()));
        initAccountRoutes();
        initTranferRoute();
        new ExceptionHandling().initExceptions();
    }

    private void initAccountRoutes() {
        initAccountsRoute();
        initAccountByIdRoute();
        initAccountCreateRoute();
    }

    private void initAccountsRoute() {
        get(PathConstants.PATH_ACCOUNTS, ((req, res) -> accountService.getAllAccounts()), json);
    }

    private void initAccountByIdRoute() {
        get(PathConstants.PATH_ACCOUNTS + "/:id", ((req, res) -> accountService.findAccount(req.params(":id"))), json);
    }

    private void initAccountCreateRoute() {
        post(PathConstants.PATH_ACCOUNTS, ((req, res) -> {
            Account account = gson.fromJson(req.body(), Account.class);
            Account createdAccount = accountService.createAccount(account.getBalance());
            res.status(HTTP_CREATED);
            return createdAccount;
        }), json);
    }

    private void initTranferRoute() {
        post(PathConstants.PATH_TRANSFER, ((req, res) -> {
            TransferRequest transferRequest = gson.fromJson(req.body(), TransferRequest.class);
            Account firstAccount = accountService.findAccount(transferRequest.getFromAccountId());
            Account secondAccount = accountService.findAccount(transferRequest.getToAccountId());
            TransferResponse transferResponse = moneyTransferService
                    .moneyTransfer(firstAccount, secondAccount, transferRequest.getAmount());
            res.status(HTTP_OK);
            return transferResponse;
        }), json);
    }

    private class ExceptionHandling {
        private void initExceptions() {

            initException(ParamIsNotNumberException.class);
            initException(NotEnoughMoneyException.class);
            initException(NegativeAmountException.class);
            initException(SameAccountException.class);

            exception(JsonSyntaxException.class, (e, request, response) ->
                    fillResponseBadRequest(response, "Failed to parse Json"));

            exception(AccountNotFoundException.class, (e, request, response) ->
                    fillResponse(response, HTTP_NOT_FOUND, e.getMessage()));

            exception(Exception.class, (e, request, response) ->
                    fillResponse(response, HTTP_INTERNAL_ERROR, "500 Internal Error"));
        }

        private void fillResponse(Response response, int errorCode, String message) {
            response.status(errorCode);
            response.body(message);
        }

        private void fillResponseBadRequest(Response response, String message) {
            fillResponse(response, HTTP_BAD_REQUEST, message);
        }

        private <T extends Exception> void initException(Class<T> exceptionClass) {
            exception(exceptionClass, (e, request, response) ->
                    fillResponseBadRequest(response, e.getMessage()));
        }
    }
}

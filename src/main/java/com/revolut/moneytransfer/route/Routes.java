package com.revolut.moneytransfer.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.revolut.moneytransfer.JsonTransformer;
import com.revolut.moneytransfer.exception.AccountNotFoundException;
import com.revolut.moneytransfer.exception.NegativeAmountException;
import com.revolut.moneytransfer.exception.NotEnoughMoneyException;
import com.revolut.moneytransfer.model.Account;
import com.revolut.moneytransfer.model.Transfer;
import com.revolut.moneytransfer.repository.AccountRepositoryInMemory;
import com.revolut.moneytransfer.service.AccountService;
import com.revolut.moneytransfer.service.MoneyTransferService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import spark.Response;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
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

    public void initRoutes() {
        before((req, res) -> res.type("application/json"));
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
        get("/accounts", ((req, res) -> accountService.getAllAccounts()), json);
    }

    private void initAccountByIdRoute() {
        get("/accounts/:id", ((req, res) -> accountService.findAccount(Long.parseLong(req.params(":id")))), json);
    }

    private void initAccountCreateRoute() {
        post("/accounts", ((req, res) -> {
            Account account = gson.fromJson(req.body(), Account.class);
            accountService.save(account);
            res.status(HTTP_CREATED);
            return account;
        }), json);
    }

    private void initTranferRoute() {
        post("/transfer", ((req, res) -> {
            Transfer transfer = gson.fromJson(req.body(), Transfer.class);
            Account firstAccount = accountService.findAccount(transfer.getFromAccountId());
            Account secondAccount = accountService.findAccount(transfer.getToAccountId());
            moneyTransferService.moneyTransfer(firstAccount, secondAccount, transfer.getAmount());
            res.status(HTTP_OK);
            return transfer;
        }), json);
    }

    private class ExceptionHandling {
        private void initExceptions() {
            exception(NumberFormatException.class, (e, request, response) ->
                    fillResponseBadRequestWithMessage(response,"Param is not a number"));

            exception(JsonSyntaxException.class, (e, request, response) ->
                    fillResponseBadRequestWithMessage(response,"Failed to parse Json"));

            exception(NotEnoughMoneyException.class, (e, request, response) -> fillResponseBadRequest(e, response));

            exception(AccountNotFoundException.class, (e, request, response) ->
                    fillResponse(response, HTTP_NOT_FOUND, e.getMessage()));

            exception(NotEnoughMoneyException.class, (e, request, response) -> fillResponseBadRequest(e, response));

            exception(NegativeAmountException.class, (e, request, response) -> fillResponseBadRequest(e, response));

            exception(Exception.class, (e, request, response) -> fillResponse(response, HTTP_INTERNAL_ERROR, "500 Internal Error"));
        }

        private void fillResponseBadRequest(Exception e, Response response) {
            fillResponse(response, HTTP_BAD_REQUEST, e.getMessage());
        }

        private void fillResponse(Response response, int errorCode, String message) {
            response.status(errorCode);
            response.body(message);
        }

        private void fillResponseBadRequestWithMessage(Response response, String message) {
            fillResponse(response, HTTP_BAD_REQUEST, message);
        }
    }
}

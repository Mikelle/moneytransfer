package com.revolut.moneytransfer.exception;

public class SameAccountException extends Exception {
    @Override
    public String getMessage() {
        return "Can't transfer money to same account";
    }
}

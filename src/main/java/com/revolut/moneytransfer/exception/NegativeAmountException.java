package com.revolut.moneytransfer.exception;

public class NegativeAmountException extends Exception {
    @Override
    public String getMessage() {
        return "Amount is negative";
    }
}

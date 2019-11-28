package com.revolut.payments.exception;

public class NotEnoughMoneyException extends Exception {
    public NotEnoughMoneyException( String message ) {
        super(message);
    }
}

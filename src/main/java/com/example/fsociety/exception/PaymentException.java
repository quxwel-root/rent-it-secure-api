package com.example.fsociety.exception;

public class PaymentException extends RuntimeException {
    public PaymentException (String message) {
        super(message);
    }
}
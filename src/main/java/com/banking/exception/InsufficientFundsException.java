package com.banking.exception;

/**
 * Exception thrown when an account attempts a withdrawal or transfer out 
 * that exceeds its currently available balance, preventing overdrafts.
 */
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
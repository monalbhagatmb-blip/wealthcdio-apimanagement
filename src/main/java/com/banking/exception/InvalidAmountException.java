package com.banking.exception;

/**
 * Exception thrown when a transaction amount violates business validations 
 * (e.g., negative amounts, zero values, or self-transfers).
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}

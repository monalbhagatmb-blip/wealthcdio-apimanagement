package com.banking.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidAmountException.class, InsufficientFundsException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBusinessViolations(RuntimeException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleMissingElements(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
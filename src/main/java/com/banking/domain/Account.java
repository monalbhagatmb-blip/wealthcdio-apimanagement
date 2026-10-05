package com.banking.domain;

import com.banking.exception.InsufficientFundsException;
import com.banking.exception.InvalidAmountException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String id;
    private BigDecimal balance;
    private final List<Transaction> ledger;

    public Account(String id, BigDecimal initialBalance) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty.");
        }
        validateAmountPositive(initialBalance);
        this.id = id;
        this.balance = initialBalance;
        this.ledger = new ArrayList<>();
    }

    public synchronized void deposit(BigDecimal amount, String referenceAccountId) {
        validateAmountPositive(amount);
        this.balance = this.balance.add(amount);
        this.ledger.add(new Transaction(TransactionType.DEPOSIT, amount, referenceAccountId));
    }

    public synchronized void withdraw(BigDecimal amount, String referenceAccountId) {
        validateAmountPositive(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal.");
        }
        this.balance = this.balance.subtract(amount);
        this.ledger.add(new Transaction(TransactionType.WITHDRAWAL, amount, referenceAccountId));
    }

    public synchronized void transferOut(BigDecimal amount, String destinationAccountId) {
        validateAmountPositive(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer.");
        }
        this.balance = this.balance.subtract(amount);
        this.ledger.add(new Transaction(TransactionType.TRANSFER_OUT, amount, destinationAccountId));
    }

    public synchronized void transferIn(BigDecimal amount, String sourceAccountId) {
        validateAmountPositive(amount);
        this.balance = this.balance.add(amount);
        this.ledger.add(new Transaction(TransactionType.TRANSFER_IN, amount, sourceAccountId));
    }

    private void validateAmountPositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero.");
        }
    }

    public String getId() { return id; }
    public synchronized BigDecimal getBalance() { return balance; }
    public synchronized List<Transaction> getLedger() { 
        return Collections.unmodifiableList(new ArrayList<>(ledger)); 
    }
}
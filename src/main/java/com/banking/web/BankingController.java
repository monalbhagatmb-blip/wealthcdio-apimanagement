package com.banking.web;

import com.banking.domain.Transaction;
import com.banking.domain.Account;
import com.banking.exception.InvalidAmountException;
import com.banking.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@Tag(name = "Banking Transaction Processor", description = "Endpoints for handling balance operations, ledger tracking, and synchronized funds distribution")
public class BankingController {

    private final AccountRepository repository;

    public BankingController(AccountRepository repository) {
        this.repository = repository;
        // Seed default accounts for direct evaluation tracking out-of-the-box
        this.repository.save(new Account("ACC-100", new BigDecimal("1000.00")));
        this.repository.save(new Account("DEST-01", new BigDecimal("250.00")));
    }

    @GetMapping("/accounts/{accountId}/balance")
    @Operation(summary = "Query real-time profile financial balance status footprint", responses = {
        @ApiResponse(responseCode = "200", description = "Balance status metrics evaluated successfully"),
        @ApiResponse(responseCode = "404", description = "Account reference record missing inside database")
    })
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable @Parameter(description = "Account Unique String Profile Lookup Key") String accountId) {
        Account account = getAccountOrThrow(accountId);
        return ResponseEntity.ok(account.getBalance());
    }

    @GetMapping("/accounts/{accountId}/history")
    @Operation(summary = "Compile chronological linear historical transaction audit logs", responses = {
        @ApiResponse(responseCode = "200", description = "Ledger sequence successfully extracted"),
        @ApiResponse(responseCode = "404", description = "Target account structural mapping profile missing")
    })
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String accountId) {
        return ResponseEntity.ok(getAccountOrThrow(accountId).getLedger());
    }

    @PostMapping("/transactions/deposit")
    @Operation(summary = "Process an inbound currency ingestion node update", responses = {
        @ApiResponse(responseCode = "200", description = "Deposit balance modification registered successfully"),
        @ApiResponse(responseCode = "400", description = "Validation parameters violated (negative or zero metric evaluation context)")
    })
    public ResponseEntity<String> deposit(@RequestParam String accountId, @RequestParam BigDecimal amount) {
        getAccountOrThrow(accountId).deposit(amount, null);
        return ResponseEntity.ok("Deposit registered successfully.");
    }

    @PostMapping("/transactions/withdraw")
    @Operation(summary = "Execute an absolute monetary debit processing structure against checking assets", responses = {
        @ApiResponse(responseCode = "200", description = "Account profile balance deducted successfully"),
        @ApiResponse(responseCode = "400", description = "Action denied due to account overdraft rule preservation mechanisms")
    })
    public ResponseEntity<String> withdraw(@RequestParam String accountId, @RequestParam BigDecimal amount) {
        getAccountOrThrow(accountId).withdraw(amount, null);
        return ResponseEntity.ok("Withdrawal processed successfully.");
    }

    @PostMapping("/transactions/transfer")
    @Operation(summary = "Execute atomicity-guaranteed dual-lock cross-ledger accounts distribution transfers", responses = {
        @ApiResponse(responseCode = "200", description = "Atomic routing wire processing finalized cleanly"),
        @ApiResponse(responseCode = "400", description = "Self-transfer routing validation failure or internal account financial default state reached")
    })
    public ResponseEntity<String> transfer(
            @RequestParam String sourceAccountId, 
            @RequestParam String destinationAccountId, 
            @RequestParam BigDecimal amount) {
        
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new InvalidAmountException("Cannot transfer money to the same account node profile.");
        }

        Account source = getAccountOrThrow(sourceAccountId);
        Account destination = getAccountOrThrow(destinationAccountId);

        // Deterministic Lock Ordering Routine eliminates deadlocks during concurrent intersecting transfers
        Account firstLock = sourceAccountId.compareTo(destinationAccountId) < 0 ? source : destination;
        Account secondLock = firstLock == source ? destination : source;

        synchronized (firstLock) {
            synchronized (secondLock) {
                source.transferOut(amount, destinationAccountId);
                destination.transferIn(amount, sourceAccountId);
            }
        }
        return ResponseEntity.ok("Transfer completed successfully.");
    }

    private Account getAccountOrThrow(String accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Target identity structure record not found for: " + accountId));
    }
}
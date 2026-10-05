package com.banking;

import com.banking.domain.Account;
import com.banking.domain.TransactionType;
import com.banking.exception.InvalidAmountException;
import com.banking.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankingApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AccountRepository repository;

    @BeforeEach
    void cleanState() {
        repository.save(new Account("ACC-100", new BigDecimal("1000.00")));
        repository.save(new Account("DEST-01", new BigDecimal("250.00")));
    }

    @Test
    void testDepositEndpointThroughRESTCall() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/transactions/deposit?accountId=ACC-100&amount=500.00", null, String.class);
        
        assertEquals(200, response.getStatusCode().value());
        Account updatedAcc = repository.findById("ACC-100").orElseThrow();
        assertEquals(new BigDecimal("1500.00"), updatedAcc.getBalance());
    }

    @Test
    void testTransferLockOrderingAtomicity() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/transactions/transfer?sourceAccountId=ACC-100&destinationAccountId=DEST-01&amount=500.00", null, String.class);
        
        assertEquals(200, response.getStatusCode().value());
        assertEquals(new BigDecimal("500.00"), repository.findById("ACC-100").orElseThrow().getBalance());
        assertEquals(new BigDecimal("750.00"), repository.findById("DEST-01").orElseThrow().getBalance());
    }

    @Test
    void testWithdrawRejectsOverdraftAndTracksLedger() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/transactions/withdraw?accountId=ACC-100&amount=1500.00", null, String.class);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(new BigDecimal("1000.00"), repository.findById("ACC-100").orElseThrow().getBalance());

        Account account = new Account("TEST-01", new BigDecimal("100.00"));
        account.deposit(new BigDecimal("25.00"), "ACC-100");
        account.withdraw(new BigDecimal("50.00"), "ACC-100");

        assertEquals(2, account.getLedger().size());
        assertEquals(TransactionType.DEPOSIT, account.getLedger().get(0).type());
        assertEquals(TransactionType.WITHDRAWAL, account.getLedger().get(1).type());
    }

    @Test
    void testAccountRejectsInvalidAmountsAndSelfTransfer() {
        Account source = new Account("SOURCE", new BigDecimal("100.00"));
        Account destination = new Account("DEST", new BigDecimal("50.00"));
        repository.save(source);
        repository.save(destination);

        assertEquals("Transaction amount must be greater than zero.",
            assertThrows(InvalidAmountException.class, () -> source.deposit(BigDecimal.ZERO, null)).getMessage());
        assertEquals("Transaction amount must be greater than zero.",
            assertThrows(InvalidAmountException.class, () -> source.withdraw(new BigDecimal("-10.00"), null)).getMessage());
        assertEquals("Transaction amount must be greater than zero.",
            assertThrows(InvalidAmountException.class, () -> source.transferOut(BigDecimal.ZERO, "DEST")).getMessage());
        assertEquals("Transaction amount must be greater than zero.",
            assertThrows(InvalidAmountException.class, () -> source.transferIn(new BigDecimal("-5.00"), "DEST")).getMessage());

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/transactions/transfer?sourceAccountId=SOURCE&destinationAccountId=SOURCE&amount=10.00", null, String.class);

        assertEquals(400, response.getStatusCode().value());
        assertEquals(new BigDecimal("100.00"), repository.findById("SOURCE").orElseThrow().getBalance());
        assertEquals(new BigDecimal("50.00"), repository.findById("DEST").orElseThrow().getBalance());
    }
}
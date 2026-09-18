package com.banking;

import com.banking.domain.Account;
import com.banking.domain.TransactionType;
import com.banking.exception.InsufficientFundsException;
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
}
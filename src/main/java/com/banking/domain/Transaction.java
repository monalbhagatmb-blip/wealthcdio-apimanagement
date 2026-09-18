package com.banking.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Details regarding an absolute historical ledger statement item")
public record Transaction(
    @Schema(description = "Auto-generated unique UUID lookup key", example = "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
    String id,
    
    @Schema(description = "Operational transaction variant", example = "DEPOSIT")
    TransactionType type,
    
    @Schema(description = "Mathematical absolute dollar calculation vector", example = "150.50")
    BigDecimal amount,
    
    @Schema(description = "Precise temporal capture footprint index", example = "2026-09-18T15:45:30.123Z")
    Instant timestamp,
    
    @Schema(description = "Associated counterpart node reference target string context if applicable", example = "DEST-01", nullable = true)
    String referenceAccountId
) {
    public Transaction(TransactionType type, BigDecimal amount, String referenceAccountId) {
        this(UUID.randomUUID().toString(), type, amount, Instant.now(), referenceAccountId);
    }
}
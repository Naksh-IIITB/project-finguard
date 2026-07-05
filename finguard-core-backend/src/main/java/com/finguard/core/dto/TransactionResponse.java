package com.finguard.core.dto;

import com.finguard.core.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long userId,
        BigDecimal amount,
        String category,
        String merchant,
        LocalDateTime transactionTime,
        boolean flagged,
        double riskScore,
        LocalDateTime createdAt
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getMerchant(),
                transaction.getTransactionTime(),
                transaction.isFlagged(),
                transaction.getRiskScore(),
                transaction.getCreatedAt()
        );
    }
}

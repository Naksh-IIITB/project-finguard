package com.finguard.core.dto;

import java.math.BigDecimal;

public record TransactionContextResponse(
        Long userId,
        BigDecimal amount,
        String category,
        BigDecimal averageTransactionAmount,
        BigDecimal categoryAverageAmount,
        BigDecimal amountVsAverage,
        BigDecimal amountVsCategoryAverage
) {
}

package com.finguard.core.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public record SpendingProfileResponse(
        Long userId,
        long transactionCount,
        BigDecimal averageTransactionAmount,
        BigDecimal currentMonthSpend,
        YearMonth month
) {
}

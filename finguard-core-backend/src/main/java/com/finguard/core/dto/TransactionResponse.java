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
        String riskLevel,
        String whatChanged,
        String safestNextStep,
        LocalDateTime createdAt
) {

    public static TransactionResponse from(Transaction transaction) {
        String riskLevel = resolveRiskLevel(transaction);
        String whatChanged = resolveWhatChanged(transaction, riskLevel);
        String safestNextStep = resolveSafestNextStep(transaction, riskLevel);

        return new TransactionResponse(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getMerchant(),
                transaction.getTransactionTime(),
                transaction.isFlagged(),
                transaction.getRiskScore(),
                riskLevel,
                whatChanged,
                safestNextStep,
                transaction.getCreatedAt()
        );
    }

    private static String resolveRiskLevel(Transaction transaction) {
        if (!transaction.isFlagged() && hasUsefulText(transaction.getRiskLevel())) {
            return transaction.getRiskLevel();
        }

        if (transaction.getRiskScore() >= 0.75) {
            return "Urgent";
        }
        if (transaction.getRiskScore() >= 0.55) {
            return "Attention";
        }
        if (transaction.getRiskScore() >= 0.35 || transaction.isFlagged()) {
            return "Watch";
        }
        return "Stable";
    }

    private static String resolveWhatChanged(Transaction transaction, String riskLevel) {
        if (hasUsefulText(transaction.getWhatChanged()) && !isStaleStableCopy(transaction)) {
            return transaction.getWhatChanged();
        }

        int riskPercent = (int) Math.round(transaction.getRiskScore() * 100);
        if (transaction.isFlagged()) {
            return "%s signal: the model flagged this %s transaction with a %d%% risk score."
                    .formatted(riskLevel, transaction.getCategory(), riskPercent);
        }
        return "Stable signal: this transaction is close to your normal pattern.";
    }

    private static String resolveSafestNextStep(Transaction transaction, String riskLevel) {
        if (hasUsefulText(transaction.getSafestNextStep()) && !isStaleStableCopy(transaction)) {
            return transaction.getSafestNextStep();
        }

        return switch (riskLevel) {
            case "Urgent" -> "Verify the merchant now. If this was not you, freeze the card and contact support.";
            case "Attention" -> "Check the receipt and merchant. Mark it expected only if you recognize the charge.";
            case "Watch" -> "Keep this on your watchlist and compare it with nearby transactions before spending more.";
            default -> "No action needed; keep monitoring for changes.";
        };
    }

    private static boolean hasUsefulText(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean isStaleStableCopy(Transaction transaction) {
        return transaction.isFlagged() && "Stable".equals(transaction.getRiskLevel());
    }
}

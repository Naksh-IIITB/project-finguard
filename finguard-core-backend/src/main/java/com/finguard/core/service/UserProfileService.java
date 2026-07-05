package com.finguard.core.service;

import com.finguard.core.dto.SpendingProfileResponse;
import com.finguard.core.dto.TransactionContextResponse;
import com.finguard.core.exception.ResourceNotFoundException;
import com.finguard.core.repository.TransactionRepository;
import com.finguard.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
public class UserProfileService {

    private static final int MONEY_SCALE = 2;
    private static final int RATIO_SCALE = 4;

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public UserProfileService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public SpendingProfileResponse getSpendingProfile(Long userId) {
        ensureUserExists(userId);

        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime nextMonthStart = currentMonth.plusMonths(1).atDay(1).atStartOfDay();

        return new SpendingProfileResponse(
                userId,
                transactionRepository.countByUserId(userId),
                money(transactionRepository.findAverageAmountByUserId(userId)),
                money(transactionRepository.findTotalAmountByUserIdBetween(userId, monthStart, nextMonthStart)),
                currentMonth
        );
    }

    @Transactional(readOnly = true)
    public TransactionContextResponse buildTransactionContext(Long userId, BigDecimal amount, String category) {
        ensureUserExists(userId);

        BigDecimal averageAmount = money(transactionRepository.findAverageAmountByUserId(userId));
        BigDecimal categoryAverage = money(transactionRepository.findAverageAmountByUserIdAndCategory(userId, category));

        return new TransactionContextResponse(
                userId,
                money(amount),
                category,
                averageAmount,
                categoryAverage,
                ratio(amount, averageAmount),
                ratio(amount, categoryAverage)
        );
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal ratio(BigDecimal amount, BigDecimal average) {
        if (average == null || average.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(RATIO_SCALE, RoundingMode.HALF_UP);
        }

        return amount.divide(average, RATIO_SCALE, RoundingMode.HALF_UP);
    }
}

package com.finguard.core.service;

import com.finguard.core.dto.SpendingProfileResponse;
import com.finguard.core.dto.TransactionContextResponse;
import com.finguard.core.exception.ResourceNotFoundException;
import com.finguard.core.repository.TransactionRepository;
import com.finguard.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void getSpendingProfileReturnsAggregatedMetrics() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.countByUserId(1L)).thenReturn(3L);
        when(transactionRepository.findAverageAmountByUserId(1L)).thenReturn(new BigDecimal("200.125"));
        when(transactionRepository.findTotalAmountByUserIdBetween(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new BigDecimal("600.50"));

        SpendingProfileResponse response = userProfileService.getSpendingProfile(1L);

        assertEquals(1L, response.userId());
        assertEquals(3L, response.transactionCount());
        assertEquals(new BigDecimal("200.13"), response.averageTransactionAmount());
        assertEquals(new BigDecimal("600.50"), response.currentMonthSpend());
    }

    @Test
    void buildTransactionContextReturnsRatiosAgainstHistory() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findAverageAmountByUserId(1L)).thenReturn(new BigDecimal("200.00"));
        when(transactionRepository.findAverageAmountByUserIdAndCategory(1L, "Entertainment"))
                .thenReturn(new BigDecimal("500.00"));

        TransactionContextResponse response = userProfileService.buildTransactionContext(
                1L,
                new BigDecimal("2500.00"),
                "Entertainment"
        );

        assertEquals(new BigDecimal("2500.00"), response.amount());
        assertEquals(new BigDecimal("200.00"), response.averageTransactionAmount());
        assertEquals(new BigDecimal("500.00"), response.categoryAverageAmount());
        assertEquals(new BigDecimal("12.5000"), response.amountVsAverage());
        assertEquals(new BigDecimal("5.0000"), response.amountVsCategoryAverage());
    }

    @Test
    void buildTransactionContextUsesZeroRatioWhenThereIsNoHistory() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findAverageAmountByUserId(1L)).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.findAverageAmountByUserIdAndCategory(1L, "Travel")).thenReturn(BigDecimal.ZERO);

        TransactionContextResponse response = userProfileService.buildTransactionContext(
                1L,
                new BigDecimal("1000.00"),
                "Travel"
        );

        assertEquals(new BigDecimal("0.0000"), response.amountVsAverage());
        assertEquals(new BigDecimal("0.0000"), response.amountVsCategoryAverage());
    }

    @Test
    void getSpendingProfileFailsWhenUserDoesNotExist() {
        when(userRepository.existsById(404L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userProfileService.getSpendingProfile(404L));
    }
}

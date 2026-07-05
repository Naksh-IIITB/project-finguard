package com.finguard.core.service;

import com.finguard.core.dto.AiRiskResponse;
import com.finguard.core.dto.AiRiskRequest;
import com.finguard.core.dto.CreateTransactionRequest;
import com.finguard.core.dto.TransactionContextResponse;
import com.finguard.core.dto.TransactionResponse;
import com.finguard.core.entity.Transaction;
import com.finguard.core.entity.User;
import com.finguard.core.exception.ResourceNotFoundException;
import com.finguard.core.repository.TransactionRepository;
import com.finguard.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionContextProvider transactionContextProvider;

    private final CategoryEncoder categoryEncoder = new CategoryEncoder();

    @Mock
    private RiskClient riskClient;

    @Test
    void createTransactionAppliesAiRiskResultBeforeSaving() {
        TransactionService transactionService = new TransactionService(
                transactionRepository,
                userRepository,
                transactionContextProvider,
                categoryEncoder,
                riskClient
        );
        User user = new User("demo", "demo@finguard.local", "hash");
        ReflectionTestUtils.setField(user, "id", 1L);

        LocalDateTime transactionTime = LocalDateTime.of(2026, 7, 5, 10, 30);
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("2500.00"),
                "Entertainment",
                "BookMyShow",
                transactionTime
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transactionContextProvider.buildTransactionContext(1L, request.amount(), request.category()))
                .thenReturn(new TransactionContextResponse(
                        1L,
                        new BigDecimal("2500.00"),
                        "Entertainment",
                        new BigDecimal("200.00"),
                        new BigDecimal("500.00"),
                        new BigDecimal("12.5000"),
                        new BigDecimal("5.0000")
                ));
        when(riskClient.predictRisk(any())).thenReturn(new AiRiskResponse(1L, 0.87, true));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            ReflectionTestUtils.setField(transaction, "id", 99L);
            return transaction;
        });

        TransactionResponse response = transactionService.createTransaction(1L, request);

        assertEquals(99L, response.id());
        assertEquals(1L, response.userId());
        assertEquals(new BigDecimal("2500.00"), response.amount());
        assertTrue(response.flagged());
        assertEquals(0.87, response.riskScore());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals("Entertainment", transactionCaptor.getValue().getCategory());
        assertEquals("BookMyShow", transactionCaptor.getValue().getMerchant());
        assertTrue(transactionCaptor.getValue().isFlagged());
        assertEquals(0.87, transactionCaptor.getValue().getRiskScore());

        ArgumentCaptor<AiRiskRequest> aiRequestCaptor = ArgumentCaptor.forClass(AiRiskRequest.class);
        verify(riskClient).predictRisk(aiRequestCaptor.capture());
        assertEquals(2500.0, aiRequestCaptor.getValue().amount());
        assertEquals(10, aiRequestCaptor.getValue().hourOfDay());
        assertEquals(2, aiRequestCaptor.getValue().categoryEncoded());
        assertEquals(12.5, aiRequestCaptor.getValue().amountVsAverage());
    }

    @Test
    void createTransactionFailsWhenUserDoesNotExist() {
        TransactionService transactionService = new TransactionService(
                transactionRepository,
                userRepository,
                transactionContextProvider,
                categoryEncoder,
                riskClient
        );
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("100.00"),
                "Groceries",
                "Local Store",
                LocalDateTime.of(2026, 7, 5, 9, 0)
        );

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(999L, request));
    }
}

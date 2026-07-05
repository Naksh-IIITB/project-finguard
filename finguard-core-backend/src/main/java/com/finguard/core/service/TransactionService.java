package com.finguard.core.service;

import com.finguard.core.dto.AiRiskRequest;
import com.finguard.core.dto.AiRiskResponse;
import com.finguard.core.dto.CreateTransactionRequest;
import com.finguard.core.dto.TransactionResponse;
import com.finguard.core.entity.Transaction;
import com.finguard.core.entity.User;
import com.finguard.core.exception.ResourceNotFoundException;
import com.finguard.core.repository.TransactionRepository;
import com.finguard.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TransactionService {

    private static final int TEMP_TRANSACTION_ID = 1;

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionContextProvider transactionContextProvider;
    private final CategoryEncoder categoryEncoder;
    private final RiskClient riskClient;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            TransactionContextProvider transactionContextProvider,
            CategoryEncoder categoryEncoder,
            RiskClient riskClient
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.transactionContextProvider = transactionContextProvider;
        this.categoryEncoder = categoryEncoder;
        this.riskClient = riskClient;
    }

    @Transactional
    public TransactionResponse createTransaction(Long userId, CreateTransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Transaction transaction = new Transaction(
                user,
                request.amount(),
                request.category(),
                request.merchant(),
                request.transactionTime()
        );

        AiRiskResponse riskResponse = riskClient.predictRisk(buildAiRiskRequest(userId, request));
        transaction.applyRiskResult(riskResponse.riskScore(), riskResponse.anomaly());

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    private AiRiskRequest buildAiRiskRequest(Long userId, CreateTransactionRequest request) {
        BigDecimal amountVsAverage = transactionContextProvider
                .buildTransactionContext(userId, request.amount(), request.category())
                .amountVsAverage();

        return new AiRiskRequest(
                (long) TEMP_TRANSACTION_ID,
                request.amount().doubleValue(),
                request.transactionTime().getHour(),
                categoryEncoder.encode(request.category()),
                amountVsAverage.setScale(4, RoundingMode.HALF_UP).doubleValue()
        );
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        return transactionRepository.findByUserIdOrderByTransactionTimeDesc(userId)
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransaction(Long userId, Long transactionId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
    }
}

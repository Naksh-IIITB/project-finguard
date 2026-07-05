package com.finguard.core.service;

import com.finguard.core.dto.CreateTransactionRequest;
import com.finguard.core.dto.TransactionResponse;
import com.finguard.core.entity.Transaction;
import com.finguard.core.entity.User;
import com.finguard.core.exception.ResourceNotFoundException;
import com.finguard.core.repository.TransactionRepository;
import com.finguard.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.userId()));

        Transaction transaction = new Transaction(
                user,
                request.amount(),
                request.category(),
                request.merchant(),
                request.transactionTime()
        );

        return TransactionResponse.from(transactionRepository.save(transaction));
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
    public TransactionResponse getTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
    }
}

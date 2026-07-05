package com.finguard.core.controller;

import com.finguard.core.dto.CreateTransactionRequest;
import com.finguard.core.dto.TransactionResponse;
import com.finguard.core.security.UserPrincipal;
import com.finguard.core.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return transactionService.createTransaction(userPrincipal.getId(), request);
    }

    @GetMapping
    public List<TransactionResponse> getTransactionsForUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return transactionService.getTransactionsForUser(userPrincipal.getId());
    }

    @GetMapping("/{transactionId}")
    public TransactionResponse getTransaction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long transactionId
    ) {
        return transactionService.getTransaction(userPrincipal.getId(), transactionId);
    }
}

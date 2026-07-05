package com.finguard.core.service;

import com.finguard.core.dto.TransactionContextResponse;

import java.math.BigDecimal;

public interface TransactionContextProvider {

    TransactionContextResponse buildTransactionContext(Long userId, BigDecimal amount, String category);
}

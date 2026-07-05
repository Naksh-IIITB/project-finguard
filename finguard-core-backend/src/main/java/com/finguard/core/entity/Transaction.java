package com.finguard.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, length = 120)
    private String merchant;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "is_flagged", nullable = false)
    private boolean flagged;

    @Column(name = "risk_score", nullable = false)
    private double riskScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Transaction() {
    }

    public Transaction(User user, BigDecimal amount, String category, String merchant, LocalDateTime transactionTime) {
        this.user = user;
        this.amount = amount;
        this.category = category;
        this.merchant = merchant;
        this.transactionTime = transactionTime;
        this.flagged = false;
        this.riskScore = 0.0;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getMerchant() {
        return merchant;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

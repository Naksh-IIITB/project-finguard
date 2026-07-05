package com.finguard.core.repository;

import com.finguard.core.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByTransactionTimeDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    @Query(value = "select coalesce(avg(amount), 0) from transactions where user_id = :userId", nativeQuery = true)
    BigDecimal findAverageAmountByUserId(@Param("userId") Long userId);

    @Query(value = """
            select coalesce(sum(amount), 0)
            from transactions
            where user_id = :userId
              and transaction_time >= :start
              and transaction_time < :end
            """, nativeQuery = true)
    BigDecimal findTotalAmountByUserIdBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query(value = """
            select coalesce(avg(amount), 0)
            from transactions
            where user_id = :userId
              and lower(category) = lower(:category)
            """, nativeQuery = true)
    BigDecimal findAverageAmountByUserIdAndCategory(
            @Param("userId") Long userId,
            @Param("category") String category
    );
}

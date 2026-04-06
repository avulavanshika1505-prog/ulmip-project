package com.ulmip.repository;

import com.ulmip.model.FinanceTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinanceTransactionRepository extends JpaRepository<FinanceTransaction, Long> {

    List<FinanceTransaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    @Query("SELECT f FROM FinanceTransaction f WHERE f.user.id = :userId AND f.transactionDate BETWEEN :startDate AND :endDate ORDER BY f.transactionDate DESC")
    List<FinanceTransaction> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(f.amount) FROM FinanceTransaction f WHERE f.user.id = :userId AND f.type = :type AND MONTH(f.transactionDate) = MONTH(CURRENT_DATE) AND YEAR(f.transactionDate) = YEAR(CURRENT_DATE)")
    Double getTotalCurrentMonth(@Param("userId") Long userId,
                                @Param("type") FinanceTransaction.Type type);

    @Query("SELECT f.category, SUM(f.amount) FROM FinanceTransaction f WHERE f.user.id = :userId AND f.type = 'EXPENSE' AND MONTH(f.transactionDate) = MONTH(CURRENT_DATE) GROUP BY f.category")
    List<Object[]> getExpenseByCategory(@Param("userId") Long userId);
}

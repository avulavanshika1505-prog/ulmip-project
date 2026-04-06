package com.ulmip.repository;

import com.ulmip.model.Finance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceRepository extends JpaRepository<Finance, Long> {

    List<Finance> findAllByOrderByDateDescIdDesc();

    List<Finance> findByTypeOrderByDateDesc(Finance.Type type);

    // Use enum constant reference, not string literal — required for Hibernate 6
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Finance f WHERE f.type = com.ulmip.model.Finance.Type.INCOME")
    Double totalIncome();

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Finance f WHERE f.type = com.ulmip.model.Finance.Type.EXPENSE")
    Double totalExpense();

    @Query("SELECT f.category, SUM(f.amount) FROM Finance f WHERE f.type = com.ulmip.model.Finance.Type.EXPENSE GROUP BY f.category")
    List<Object[]> expenseByCategory();
}

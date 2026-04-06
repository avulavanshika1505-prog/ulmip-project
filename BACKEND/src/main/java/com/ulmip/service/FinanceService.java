package com.ulmip.service;

import com.ulmip.model.Finance;
import com.ulmip.repository.FinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceRepository financeRepository;

    public List<Finance> getAll() {
        return financeRepository.findAllByOrderByDateDescIdDesc();
    }

    public Finance addTransaction(Finance finance) {
        return financeRepository.save(finance);
    }

    public void delete(Long id) {
        financeRepository.deleteById(id);
    }

    public double getTotalIncome() {
        Double v = financeRepository.totalIncome();
        return v != null ? v : 0.0;
    }

    public double getTotalExpense() {
        Double v = financeRepository.totalExpense();
        return v != null ? v : 0.0;
    }

    public Map<String, Double> getExpenseByCategory() {
        Map<String, Double> result = new HashMap<>();
        List<Object[]> rows = financeRepository.expenseByCategory();
        for (Object[] row : rows) {
            if (row[0] != null && row[1] != null) {
                result.put(row[0].toString(), ((Number) row[1]).doubleValue());
            }
        }
        return result;
    }
}

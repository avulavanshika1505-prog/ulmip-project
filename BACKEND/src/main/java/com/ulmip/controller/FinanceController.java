package com.ulmip.controller;

import com.ulmip.model.Finance;
import com.ulmip.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final FinanceService financeService;

    @GetMapping
    public ResponseEntity<List<Finance>> getAll() {
        return ResponseEntity.ok(financeService.getAll());
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        double income = financeService.getTotalIncome();
        double expense = financeService.getTotalExpense();
        double balance = income - expense;
        int savingsRate = income > 0 ? (int)(((income - expense) / income) * 100) : 0;

        return ResponseEntity.ok(Map.of(
            "totalIncome", income,
            "totalExpense", expense,
            "netBalance", balance,
            "savingsRate", savingsRate,
            "expenseByCategory", financeService.getExpenseByCategory()
        ));
    }

    @PostMapping
    public ResponseEntity<Finance> add(@RequestBody Finance finance) {
        return ResponseEntity.ok(financeService.addTransaction(finance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        financeService.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}

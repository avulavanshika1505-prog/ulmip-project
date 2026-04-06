package com.ulmip.controller;

import com.ulmip.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskService taskService;
    private final HealthService healthService;
    private final FinanceService financeService;
    private final GoalService goalService;
    private final EventService eventService;
    private final SuggestionService suggestionService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        long total = taskService.totalCount();
        long completed = taskService.completedCount();
        int completionRate = total > 0 ? (int) ((completed * 100.0) / total) : 0;
        double income = financeService.getTotalIncome();
        double expense = financeService.getTotalExpense();

        Map<String, Object> stats = new HashMap<>();
        stats.put("tasksCompleted", completed);
        stats.put("totalTasks", total);
        stats.put("completionRate", completionRate);
        stats.put("netBalance", income - expense);
        stats.put("avgSleep", healthService.getAvgSleep());
        stats.put("avgMood", healthService.getAvgMood());
        stats.put("activeGoals", goalService.activeGoals());
        stats.put("completedGoals", goalService.completedGoals());

        List<Object> upcomingEvents = new ArrayList<>(
                eventService.getUpcomingEvents().stream().limit(3).toList()
        );
        List<Object> highPriorityTasks = new ArrayList<>(
                taskService.getHighPriorityPending().stream().limit(3).toList()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("stats", stats);
        response.put("latestHealth", healthService.getLatest().orElse(null));
        response.put("suggestions", suggestionService.generateSuggestions());
        response.put("upcomingEvents", upcomingEvents);
        response.put("highPriorityTasks", highPriorityTasks);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<?> getSuggestions() {
        return ResponseEntity.ok(suggestionService.generateSuggestions());
    }

    @GetMapping("/health-check")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "ULMIP Backend Running");
        return ResponseEntity.ok(result);
    }
}

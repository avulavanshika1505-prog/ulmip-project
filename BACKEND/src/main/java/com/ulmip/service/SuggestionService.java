package com.ulmip.service;

import com.ulmip.model.HealthLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final TaskService taskService;
    private final HealthService healthService;
    private final FinanceService financeService;
    private final GoalService goalService;

    public List<Map<String, String>> generateSuggestions() {
        List<Map<String, String>> suggestions = new ArrayList<>();

        // Task-based suggestions
        long highPriority = taskService.getHighPriorityPending().size();
        if (highPriority > 2) {
            suggestions.add(Map.of(
                "type", "warning",
                "icon", "⚠️",
                "text", "You have " + highPriority + " high-priority tasks pending. Focus on clearing them before taking on new work."
            ));
        }

        long total = taskService.totalCount();
        long completed = taskService.completedCount();
        int completionRate = total > 0 ? (int)((completed * 100.0) / total) : 0;
        if (completionRate < 50 && total > 3) {
            suggestions.add(Map.of(
                "type", "warning",
                "icon", "📋",
                "text", "Task completion rate is only " + completionRate + "%. Try time-blocking to power through your pending tasks."
            ));
        }

        // Health-based suggestions
        Optional<HealthLog> latestOpt = healthService.getLatest();
        if (latestOpt.isPresent()) {
            HealthLog latest = latestOpt.get();
            if (latest.getSleep() < 7) {
                suggestions.add(Map.of(
                    "type", "health",
                    "icon", "😴",
                    "text", "You slept only " + latest.getSleep() + "h recently. Aim for 7–9 hours — sleep deprivation reduces cognitive performance by up to 30%."
                ));
            }
            if (latest.getWater() < 8) {
                suggestions.add(Map.of(
                    "type", "health",
                    "icon", "💧",
                    "text", "Only " + latest.getWater() + " glasses of water logged. Staying hydrated improves focus and energy significantly."
                ));
            }
            if (latest.getStress() >= 4) {
                suggestions.add(Map.of(
                    "type", "health",
                    "icon", "🧘",
                    "text", "High stress level detected (" + latest.getStress() + "/5). Consider a 10-minute mindfulness break to reset your nervous system."
                ));
            }
            if (latest.getExercise() == 0) {
                suggestions.add(Map.of(
                    "type", "health",
                    "icon", "🏃",
                    "text", "No exercise logged recently. Even a 20-minute walk boosts BDNF — your brain's growth hormone — by 30%."
                ));
            }
        } else {
            suggestions.add(Map.of(
                "type", "health",
                "icon", "❤️",
                "text", "Start logging your daily health metrics to receive personalized wellness recommendations."
            ));
        }

        // Finance suggestions
        double income = financeService.getTotalIncome();
        double expense = financeService.getTotalExpense();
        if (income > 0 && expense > income * 0.8) {
            suggestions.add(Map.of(
                "type", "finance",
                "icon", "💰",
                "text", "You're spending " + (int)((expense / income) * 100) + "% of your income. Try the 50/30/20 rule: 50% needs, 30% wants, 20% savings."
            ));
        }

        // Goal suggestions
        long activeGoals = goalService.activeGoals();
        if (activeGoals == 0) {
            suggestions.add(Map.of(
                "type", "info",
                "icon", "🎯",
                "text", "You have no active goals. Setting clear goals increases your likelihood of achievement by 42%."
            ));
        }

        if (suggestions.isEmpty()) {
            suggestions.add(Map.of(
                "type", "success",
                "icon", "🌟",
                "text", "You're doing great across all areas! Keep maintaining this balance — consistency is the key to lasting improvement."
            ));
        }

        return suggestions;
    }
}

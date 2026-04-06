package com.ulmip.controller;

import com.ulmip.model.*;
import com.ulmip.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

// ============================================================
// HEALTH CONTROLLER
// ============================================================
@RestController
@RequestMapping("/health")
class HealthController {

    @Autowired private HealthLogRepository healthLogRepository;
    @Autowired private UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public ResponseEntity<List<HealthLog>> getLogs(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(healthLogRepository.findByUserIdOrderByLogDateDesc(getUser(ud).getId()));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getToday(@AuthenticationPrincipal UserDetails ud) {
        return healthLogRepository.findByUserIdAndLogDate(getUser(ud).getId(), LocalDate.now())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null));
    }

    @GetMapping("/range")
    public ResponseEntity<List<HealthLog>> getRange(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(
            healthLogRepository.findByUserIdAndDateRange(getUser(ud).getId(), start, end));
    }

    @PostMapping
    public ResponseEntity<HealthLog> logHealth(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody HealthLog log) {
        User user = getUser(ud);
        log.setUser(user);
        if (log.getLogDate() == null) log.setLogDate(LocalDate.now());
        Optional<HealthLog> existing = healthLogRepository.findByUserIdAndLogDate(user.getId(), log.getLogDate());
        existing.ifPresent(h -> log.setId(h.getId()));
        return ResponseEntity.ok(healthLogRepository.save(log));
    }

    @GetMapping("/stress-average")
    public ResponseEntity<Map<String, Double>> getStressAvg(@AuthenticationPrincipal UserDetails ud) {
        LocalDate since = LocalDate.now().minusDays(7);
        Double avg = healthLogRepository.getAverageStressLevel(getUser(ud).getId(), since);
        return ResponseEntity.ok(Map.of("averageStress", avg != null ? avg : 0.0));
    }
}

// ============================================================
// FINANCE CONTROLLER
// ============================================================
@RestController
@RequestMapping("/finance")
class FinanceController {

    @Autowired private FinanceTransactionRepository financeRepo;
    @Autowired private UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<FinanceTransaction>> getAll(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(financeRepo.findByUserIdOrderByTransactionDateDesc(getUser(ud).getId()));
    }

    @PostMapping("/transactions")
    public ResponseEntity<FinanceTransaction> create(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody FinanceTransaction txn) {
        txn.setUser(getUser(ud));
        if (txn.getTransactionDate() == null) txn.setTransactionDate(LocalDate.now());
        return ResponseEntity.ok(financeRepo.save(txn));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@AuthenticationPrincipal UserDetails ud) {
        Long userId = getUser(ud).getId();
        Double income = financeRepo.getTotalCurrentMonth(userId, FinanceTransaction.Type.INCOME);
        Double expense = financeRepo.getTotalCurrentMonth(userId, FinanceTransaction.Type.EXPENSE);
        List<Object[]> byCategory = financeRepo.getExpenseByCategory(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("income", income != null ? income : 0.0);
        result.put("expense", expense != null ? expense : 0.0);
        result.put("balance", (income != null ? income : 0.0) - (expense != null ? expense : 0.0));

        Map<String, Double> catMap = new LinkedHashMap<>();
        byCategory.forEach(row -> catMap.put((String) row[0], ((Number) row[1]).doubleValue()));
        result.put("expenseByCategory", catMap);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) {
        User user = getUser(ud);
        return financeRepo.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(t -> {
                    financeRepo.delete(t);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

// ============================================================
// GOAL CONTROLLER
// ============================================================
@RestController
@RequestMapping("/goals")
class GoalController {

    @Autowired private GoalRepository goalRepo;
    @Autowired private UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public ResponseEntity<List<Goal>> getAll(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(goalRepo.findByUserIdOrderByCreatedAtDesc(getUser(ud).getId()));
    }

    @PostMapping
    public ResponseEntity<Goal> create(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody Goal goal) {
        goal.setUser(getUser(ud));
        return ResponseEntity.ok(goalRepo.save(goal));
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<Goal> updateProgress(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        User user = getUser(ud);
        return goalRepo.findById(id)
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .map(goal -> {
                    goal.setCurrentValue(BigDecimal.valueOf(body.get("currentValue")));
                    if (goal.getProgressPercentage() >= 100) {
                        goal.setStatus(Goal.Status.COMPLETED);
                    }
                    return ResponseEntity.ok(goalRepo.save(goal));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) {
        User user = getUser(ud);
        return goalRepo.findById(id)
                .filter(g -> g.getUser().getId().equals(user.getId()))
                .map(g -> {
                    goalRepo.delete(g);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

// ============================================================
// LEARNING CONTROLLER
// ============================================================
@RestController
@RequestMapping("/learning")
class LearningController {

    @Autowired private LearningCourseRepository courseRepo;
    @Autowired private UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public ResponseEntity<List<LearningCourse>> getAll(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(courseRepo.findByUserIdOrderByUpdatedAtDesc(getUser(ud).getId()));
    }

    @PostMapping
    public ResponseEntity<LearningCourse> create(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody LearningCourse course) {
        course.setUser(getUser(ud));
        return ResponseEntity.ok(courseRepo.save(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningCourse> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @RequestBody LearningCourse updated) {
        User user = getUser(ud);
        return courseRepo.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .map(course -> {
                    course.setCompletedModules(updated.getCompletedModules());
                    course.setSpentHours(updated.getSpentHours());
                    course.setStatus(updated.getStatus());
                    if (course.getCompletionPercentage() >= 100) {
                        course.setStatus(LearningCourse.Status.COMPLETED);
                        course.setActualCompletionDate(LocalDate.now());
                    }
                    return ResponseEntity.ok(courseRepo.save(course));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal UserDetails ud) {
        Long userId = getUser(ud).getId();
        Double totalHours = courseRepo.getTotalLearningHours(userId);
        List<LearningCourse> all = courseRepo.findByUserIdOrderByUpdatedAtDesc(userId);
        long completed = all.stream().filter(c -> c.getStatus() == LearningCourse.Status.COMPLETED).count();
        long inProgress = all.stream().filter(c -> c.getStatus() == LearningCourse.Status.IN_PROGRESS).count();
        return ResponseEntity.ok(Map.of(
            "totalHours", totalHours != null ? totalHours : 0.0,
            "totalCourses", all.size(),
            "completed", completed,
            "inProgress", inProgress
        ));
    }
}

// ============================================================
// AI SUGGESTIONS CONTROLLER
// ============================================================
@RestController
@RequestMapping("/suggestions")
class AiSuggestionController {

    @Autowired private AiSuggestionRepository suggestionRepo;
    @Autowired private UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public ResponseEntity<List<AiSuggestion>> getUnread(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(
            suggestionRepo.findByUserIdAndIsReadFalseOrderByPriorityDesc(getUser(ud).getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AiSuggestion>> getAll(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(
            suggestionRepo.findByUserIdOrderByCreatedAtDesc(getUser(ud).getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<AiSuggestion> markRead(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) {
        User user = getUser(ud);
        return suggestionRepo.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .map(s -> {
                    s.setIsRead(true);
                    return ResponseEntity.ok(suggestionRepo.save(s));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount(@AuthenticationPrincipal UserDetails ud) {
        Long count = suggestionRepo.countUnreadByUserId(getUser(ud).getId());
        return ResponseEntity.ok(Map.of("unread", count));
    }
}

// ============================================================
// DASHBOARD CONTROLLER
// ============================================================
@RestController
@RequestMapping("/dashboard")
class DashboardController {

    @Autowired private TaskRepository taskRepo;
    @Autowired private HealthLogRepository healthRepo;
    @Autowired private FinanceTransactionRepository financeRepo;
    @Autowired private GoalRepository goalRepo;
    @Autowired private LearningCourseRepository courseRepo;
    @Autowired private AiSuggestionRepository suggestionRepo;
    @Autowired private UserRepository userRepository;

    private User getUser(UserDetails ud) {
        return userRepository.findByEmail(ud.getUsername()).orElseThrow();
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview(@AuthenticationPrincipal UserDetails ud) {
        Long userId = getUser(ud).getId();
        Map<String, Object> overview = new LinkedHashMap<>();

        overview.put("tasksTodo", taskRepo.countByUserIdAndStatus(userId, Task.Status.TODO));
        overview.put("tasksInProgress", taskRepo.countByUserIdAndStatus(userId, Task.Status.IN_PROGRESS));
        overview.put("tasksCompleted", taskRepo.countByUserIdAndStatus(userId, Task.Status.COMPLETED));

        healthRepo.findByUserIdAndLogDate(userId, LocalDate.now()).ifPresent(h -> {
            overview.put("todayMood", h.getMood());
            overview.put("todayStress", h.getStressLevel());
            overview.put("todaySteps", h.getSteps());
            overview.put("todayWater", h.getWaterMl());
            overview.put("todaySleep", h.getSleepHours());
            overview.put("todayEnergy", h.getEnergyLevel());
        });

        Double income = financeRepo.getTotalCurrentMonth(userId, FinanceTransaction.Type.INCOME);
        Double expense = financeRepo.getTotalCurrentMonth(userId, FinanceTransaction.Type.EXPENSE);
        overview.put("monthlyIncome", income != null ? income : 0.0);
        overview.put("monthlyExpense", expense != null ? expense : 0.0);
        overview.put("monthlyBalance",
            (income != null ? income : 0.0) - (expense != null ? expense : 0.0));

        List<Goal> activeGoals = goalRepo.findByUserIdAndStatusOrderByTargetDateAsc(userId, Goal.Status.ACTIVE);
        overview.put("activeGoals", activeGoals.size());

        Double learningHours = courseRepo.getTotalLearningHours(userId);
        overview.put("totalLearningHours", learningHours != null ? learningHours : 0.0);

        overview.put("unreadSuggestions", suggestionRepo.countUnreadByUserId(userId));

        return ResponseEntity.ok(overview);
    }
}

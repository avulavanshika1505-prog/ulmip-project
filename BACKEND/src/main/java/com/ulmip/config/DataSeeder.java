package com.ulmip.config;

import com.ulmip.model.*;
import com.ulmip.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository taskRepo;
    private final EventRepository eventRepo;
    private final HealthLogRepository healthRepo;
    private final FinanceRepository financeRepo;
    private final GoalRepository goalRepo;

    @Override
    public void run(String... args) {
        // Only seed if database is empty
        if (taskRepo.count() > 0) {
            log.info("📦 Database already has data — skipping seeder");
            return;
        }

        log.info("🌱 Seeding initial data...");

        // Tasks
        taskRepo.save(Task.builder().title("Study Data Structures & Algorithms").category(Task.Category.ACADEMIC).priority(Task.Priority.HIGH).dueDate(LocalDate.now().plusDays(3)).done(false).build());
        taskRepo.save(Task.builder().title("Morning Run 5km").category(Task.Category.HEALTH).priority(Task.Priority.MEDIUM).dueDate(LocalDate.now()).done(false).build());
        taskRepo.save(Task.builder().title("Pay Electricity Bill").category(Task.Category.FINANCE).priority(Task.Priority.HIGH).dueDate(LocalDate.now().minusDays(1)).done(true).build());
        taskRepo.save(Task.builder().title("Read 2 chapters of Clean Code").category(Task.Category.ACADEMIC).priority(Task.Priority.MEDIUM).dueDate(LocalDate.now().plusDays(5)).done(false).build());
        taskRepo.save(Task.builder().title("Complete Mini Project Frontend").category(Task.Category.WORK).priority(Task.Priority.HIGH).dueDate(LocalDate.now().plusDays(7)).done(false).build());

        // Events
        eventRepo.save(Event.builder().title("Team Meeting").date(LocalDate.now().plusDays(1)).time(LocalTime.of(10, 0)).color("#6C63FF").build());
        eventRepo.save(Event.builder().title("Gym Session").date(LocalDate.now().plusDays(2)).time(LocalTime.of(7, 0)).color("#00C9A7").build());
        eventRepo.save(Event.builder().title("Project Submission").date(LocalDate.now().plusDays(7)).time(LocalTime.of(23, 59)).color("#FF6584").build());
        eventRepo.save(Event.builder().title("Monthly Budget Review").date(LocalDate.now().plusDays(10)).time(LocalTime.of(19, 0)).color("#FFB347").build());

        // Health logs
        healthRepo.save(HealthLog.builder().date(LocalDate.now()).sleep(7.5).water(8).exercise(30).mood(4).stress(3).build());
        healthRepo.save(HealthLog.builder().date(LocalDate.now().minusDays(1)).sleep(6.0).water(6).exercise(0).mood(3).stress(5).build());
        healthRepo.save(HealthLog.builder().date(LocalDate.now().minusDays(2)).sleep(8.0).water(10).exercise(60).mood(5).stress(2).build());
        healthRepo.save(HealthLog.builder().date(LocalDate.now().minusDays(3)).sleep(7.0).water(8).exercise(20).mood(4).stress(3).build());

        // Finance
        financeRepo.save(Finance.builder().title("Freelance Payment").type(Finance.Type.INCOME).amount(5000).category(Finance.Category.WORK).date(LocalDate.now().minusDays(2)).build());
        financeRepo.save(Finance.builder().title("Grocery Shopping").type(Finance.Type.EXPENSE).amount(850).category(Finance.Category.FOOD).date(LocalDate.now()).build());
        financeRepo.save(Finance.builder().title("Netflix Subscription").type(Finance.Type.EXPENSE).amount(199).category(Finance.Category.ENTERTAINMENT).date(LocalDate.now()).build());
        financeRepo.save(Finance.builder().title("Electricity Bill").type(Finance.Type.EXPENSE).amount(1200).category(Finance.Category.UTILITIES).date(LocalDate.now().minusDays(1)).build());
        financeRepo.save(Finance.builder().title("College Fee Stipend").type(Finance.Type.INCOME).amount(3000).category(Finance.Category.EDUCATION).date(LocalDate.now().minusDays(5)).build());

        // Goals
        goalRepo.save(Goal.builder().title("Complete Mini Project").category(Goal.Category.ACADEMIC).target(100).progress(65).unit("%").deadline(LocalDate.now().plusDays(15)).build());
        goalRepo.save(Goal.builder().title("Read 10 Books This Year").category(Goal.Category.PERSONAL).target(10).progress(3).unit("books").deadline(LocalDate.now().plusMonths(9)).build());
        goalRepo.save(Goal.builder().title("Save ₹50,000").category(Goal.Category.FINANCE).target(50000).progress(18000).unit("₹").deadline(LocalDate.now().plusMonths(4)).build());
        goalRepo.save(Goal.builder().title("Run 100km Total").category(Goal.Category.FITNESS).target(100).progress(23).unit("km").deadline(LocalDate.now().plusMonths(3)).build());

        log.info("✅ Data seeded successfully!");
    }
}

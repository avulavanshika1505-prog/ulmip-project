package com.ulmip.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "daily_work_hours")
    private Integer dailyWorkHours = 8;

    @Column(name = "work_start_time")
    private LocalTime workStartTime = LocalTime.of(9, 0);

    @Column(name = "work_end_time")
    private LocalTime workEndTime = LocalTime.of(17, 0);

    @Column(name = "pomodoro_duration")
    private Integer pomodoroDuration = 25;

    @Column(name = "short_break_duration")
    private Integer shortBreakDuration = 5;

    @Column(name = "long_break_duration")
    private Integer longBreakDuration = 15;

    @Column(name = "water_goal_ml")
    private Integer waterGoalMl = 2000;

    @Column(name = "sleep_goal_hours", precision = 4, scale = 2)
    private BigDecimal sleepGoalHours = new BigDecimal("8.0");

    @Column(name = "step_goal")
    private Integer stepGoal = 10000;

    @Column(name = "calorie_goal")
    private Integer calorieGoal = 2000;

    @Column(name = "monthly_budget", precision = 12, scale = 2)
    private BigDecimal monthlyBudget = new BigDecimal("50000.00");

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled = true;

    @Column(name = "ai_suggestions_enabled")
    private Boolean aiSuggestionsEnabled = true;

    @Column(length = 20)
    private String theme = "dark";

    // Constructors
    public UserSettings() {}

    // Builder
    public static UserSettingsBuilder builder() { return new UserSettingsBuilder(); }

    public static class UserSettingsBuilder {
        private User user;
        public UserSettingsBuilder user(User user) { this.user = user; return this; }
        public UserSettings build() {
            UserSettings s = new UserSettings();
            s.user = this.user;
            return s;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getDailyWorkHours() { return dailyWorkHours; }
    public void setDailyWorkHours(Integer dailyWorkHours) { this.dailyWorkHours = dailyWorkHours; }

    public LocalTime getWorkStartTime() { return workStartTime; }
    public void setWorkStartTime(LocalTime workStartTime) { this.workStartTime = workStartTime; }

    public LocalTime getWorkEndTime() { return workEndTime; }
    public void setWorkEndTime(LocalTime workEndTime) { this.workEndTime = workEndTime; }

    public Integer getPomodoroDuration() { return pomodoroDuration; }
    public void setPomodoroDuration(Integer pomodoroDuration) { this.pomodoroDuration = pomodoroDuration; }

    public Integer getShortBreakDuration() { return shortBreakDuration; }
    public void setShortBreakDuration(Integer shortBreakDuration) { this.shortBreakDuration = shortBreakDuration; }

    public Integer getLongBreakDuration() { return longBreakDuration; }
    public void setLongBreakDuration(Integer longBreakDuration) { this.longBreakDuration = longBreakDuration; }

    public Integer getWaterGoalMl() { return waterGoalMl; }
    public void setWaterGoalMl(Integer waterGoalMl) { this.waterGoalMl = waterGoalMl; }

    public BigDecimal getSleepGoalHours() { return sleepGoalHours; }
    public void setSleepGoalHours(BigDecimal sleepGoalHours) { this.sleepGoalHours = sleepGoalHours; }

    public Integer getStepGoal() { return stepGoal; }
    public void setStepGoal(Integer stepGoal) { this.stepGoal = stepGoal; }

    public Integer getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(Integer calorieGoal) { this.calorieGoal = calorieGoal; }

    public BigDecimal getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(BigDecimal monthlyBudget) { this.monthlyBudget = monthlyBudget; }

    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }

    public Boolean getAiSuggestionsEnabled() { return aiSuggestionsEnabled; }
    public void setAiSuggestionsEnabled(Boolean aiSuggestionsEnabled) { this.aiSuggestionsEnabled = aiSuggestionsEnabled; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}

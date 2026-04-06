package com.ulmip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tasks")
public class Task {

    public enum Status { TODO, IN_PROGRESS, COMPLETED, CANCELLED }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status = Status.TODO;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "auto_priority_score")
    private Double autoPriorityScore = 0.0;

    @Column(length = 100)
    private String category;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes = 30;

    @Column(name = "actual_minutes")
    private Integer actualMinutes;

    @Column(columnDefinition = "JSON")
    private String tags;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateAutoPriorityScore();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == Status.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
        calculateAutoPriorityScore();
    }

    private void calculateAutoPriorityScore() {
        double score = 0.0;
        if (priority != null) {
            switch (priority) {
                case CRITICAL: score += 40.0; break;
                case HIGH:     score += 30.0; break;
                case MEDIUM:   score += 20.0; break;
                case LOW:      score += 10.0; break;
            }
        }
        if (dueDate != null) {
            long daysUntilDue = ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);
            if (daysUntilDue <= 0)      score += 40.0;
            else if (daysUntilDue <= 1) score += 30.0;
            else if (daysUntilDue <= 3) score += 20.0;
            else if (daysUntilDue <= 7) score += 10.0;
        }
        if (status == Status.IN_PROGRESS) score += 10.0;
        this.autoPriorityScore = Math.min(score, 100.0);
    }

    // Builder
    public static TaskBuilder builder() { return new TaskBuilder(); }

    public static class TaskBuilder {
        private User user;
        private String title;
        private String description;
        private Status status = Status.TODO;
        private Priority priority = Priority.MEDIUM;
        private String category;
        private LocalDateTime dueDate;
        private Integer estimatedMinutes = 30;
        private String tags;

        public TaskBuilder user(User user) { this.user = user; return this; }
        public TaskBuilder title(String title) { this.title = title; return this; }
        public TaskBuilder description(String description) { this.description = description; return this; }
        public TaskBuilder status(Status status) { this.status = status; return this; }
        public TaskBuilder priority(Priority priority) { this.priority = priority; return this; }
        public TaskBuilder category(String category) { this.category = category; return this; }
        public TaskBuilder dueDate(LocalDateTime dueDate) { this.dueDate = dueDate; return this; }
        public TaskBuilder estimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; return this; }
        public TaskBuilder tags(String tags) { this.tags = tags; return this; }

        public Task build() {
            Task task = new Task();
            task.user = this.user;
            task.title = this.title;
            task.description = this.description;
            task.status = this.status;
            task.priority = this.priority;
            task.category = this.category;
            task.dueDate = this.dueDate;
            task.estimatedMinutes = this.estimatedMinutes;
            task.tags = this.tags;
            return task;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Double getAutoPriorityScore() { return autoPriorityScore; }
    public void setAutoPriorityScore(Double autoPriorityScore) { this.autoPriorityScore = autoPriorityScore; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public Integer getActualMinutes() { return actualMinutes; }
    public void setActualMinutes(Integer actualMinutes) { this.actualMinutes = actualMinutes; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

package com.ulmip.controller;

import com.ulmip.model.Task;
import com.ulmip.model.User;
import com.ulmip.repository.TaskRepository;
import com.ulmip.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

class TaskRequest {
    @NotBlank
    private String title;
    private String description;
    private Task.Priority priority = Task.Priority.MEDIUM;
    private Task.Status status = Task.Status.TODO;
    private String category;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dueDate;
    private Integer estimatedMinutes = 30;
    private String tags;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Task.Priority getPriority() { return priority; }
    public void setPriority(Task.Priority priority) { this.priority = priority; }

    public Task.Status getStatus() { return status; }
    public void setStatus(Task.Status status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(taskRepository.findByUserIdOrderByAutoPriorityScoreDesc(user.getId()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Task.Status status) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(taskRepository.findByUserIdAndStatus(user.getId(), status));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getCurrentUser(userDetails);
        return ResponseEntity.ok(Map.of(
            "todo", taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.TODO),
            "inProgress", taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.IN_PROGRESS),
            "completed", taskRepository.countByUserIdAndStatus(user.getId(), Task.Status.COMPLETED)
        ));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody TaskRequest request) {
        User user = getCurrentUser(userDetails);
        Task task = Task.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(request.getStatus())
                .category(request.getCategory())
                .dueDate(request.getDueDate())
                .estimatedMinutes(request.getEstimatedMinutes())
                .tags(request.getTags())
                .build();
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody TaskRequest request) {
        User user = getCurrentUser(userDetails);
        return taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(task -> {
                    if (request.getTitle() != null) task.setTitle(request.getTitle());
                    if (request.getDescription() != null) task.setDescription(request.getDescription());
                    if (request.getPriority() != null) task.setPriority(request.getPriority());
                    if (request.getStatus() != null) task.setStatus(request.getStatus());
                    if (request.getCategory() != null) task.setCategory(request.getCategory());
                    if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
                    if (request.getEstimatedMinutes() != null) task.setEstimatedMinutes(request.getEstimatedMinutes());
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User user = getCurrentUser(userDetails);
        return taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(task -> {
                    task.setStatus(Task.Status.valueOf(body.get("status")));
                    return ResponseEntity.ok(taskRepository.save(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = getCurrentUser(userDetails);
        return taskRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

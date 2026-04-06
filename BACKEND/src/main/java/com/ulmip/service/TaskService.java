package com.ulmip.service;

import com.ulmip.model.Task;
import com.ulmip.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task updated) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        if (updated.getTitle() != null) task.setTitle(updated.getTitle());
        if (updated.getDescription() != null) task.setDescription(updated.getDescription());
        if (updated.getCategory() != null) task.setCategory(updated.getCategory());
        if (updated.getPriority() != null) task.setPriority(updated.getPriority());
        if (updated.getDueDate() != null) task.setDueDate(updated.getDueDate());
        task.setDone(updated.isDone());
        return taskRepository.save(task);
    }

    public Task toggleDone(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        task.setDone(!task.isDone());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public long totalCount() { return taskRepository.count(); }
    public long completedCount() { return taskRepository.countCompleted(); }

    public List<Task> getHighPriorityPending() {
        return taskRepository.findByDoneFalseAndPriorityOrderByCreatedAtDesc(Task.Priority.HIGH);
    }
}

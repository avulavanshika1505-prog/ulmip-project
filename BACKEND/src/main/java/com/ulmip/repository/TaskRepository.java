package com.ulmip.repository;

import com.ulmip.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByDoneOrderByCreatedAtDesc(boolean done);

    List<Task> findByPriorityAndDoneOrderByCreatedAtDesc(Task.Priority priority, boolean done);

    List<Task> findByCategoryOrderByCreatedAtDesc(Task.Category category);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.done = true")
    long countCompleted();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.done = false AND t.priority = com.ulmip.model.Task.Priority.HIGH")
    long countHighPriorityPending();

    List<Task> findByDoneFalseAndPriorityOrderByCreatedAtDesc(Task.Priority priority);
}

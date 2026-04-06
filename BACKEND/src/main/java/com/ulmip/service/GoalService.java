package com.ulmip.service;

import com.ulmip.model.Goal;
import com.ulmip.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<Goal> getAll() {
        return goalRepository.findAllByOrderByDeadlineAsc();
    }

    public Goal create(Goal goal) {
        return goalRepository.save(goal);
    }

    public Goal updateProgress(Long id, double progress) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found: " + id));
        goal.setProgress(progress);
        return goalRepository.save(goal);
    }

    public Goal update(Long id, Goal updated) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found: " + id));
        if (updated.getTitle() != null) goal.setTitle(updated.getTitle());
        if (updated.getCategory() != null) goal.setCategory(updated.getCategory());
        if (updated.getDeadline() != null) goal.setDeadline(updated.getDeadline());
        if (updated.getDescription() != null) goal.setDescription(updated.getDescription());
        goal.setTarget(updated.getTarget() > 0 ? updated.getTarget() : goal.getTarget());
        goal.setProgress(updated.getProgress());
        return goalRepository.save(goal);
    }

    public void delete(Long id) {
        goalRepository.deleteById(id);
    }

    public long activeGoals() { return goalRepository.countActive(); }
    public long completedGoals() { return goalRepository.countCompleted(); }
}

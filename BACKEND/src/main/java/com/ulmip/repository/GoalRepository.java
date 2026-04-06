package com.ulmip.repository;

import com.ulmip.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByUserIdAndStatusOrderByTargetDateAsc(Long userId, Goal.Status status);

    List<Goal> findByUserIdOrderByCreatedAtDesc(Long userId);
}

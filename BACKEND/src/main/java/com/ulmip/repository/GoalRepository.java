package com.ulmip.repository;

import com.ulmip.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findAllByOrderByDeadlineAsc();

    // Use JPQL expressions instead of comparing doubles — avoids floating point issues
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.progress >= g.target")
    long countCompleted();

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.progress < g.target")
    long countActive();
}

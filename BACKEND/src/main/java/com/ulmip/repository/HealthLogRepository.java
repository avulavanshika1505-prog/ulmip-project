package com.ulmip.repository;

import com.ulmip.model.HealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {
    List<HealthLog> findAllByOrderByDateDesc();
    Optional<HealthLog> findTopByOrderByDateDesc();

    @Query("SELECT AVG(h.sleep) FROM HealthLog h")
    Double avgSleep();

    @Query("SELECT AVG(h.mood) FROM HealthLog h")
    Double avgMood();

    @Query("SELECT AVG(h.stress) FROM HealthLog h")
    Double avgStress();
}

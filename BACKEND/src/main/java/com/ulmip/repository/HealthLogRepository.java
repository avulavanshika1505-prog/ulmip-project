package com.ulmip.repository;

import com.ulmip.model.HealthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {

    Optional<HealthLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    List<HealthLog> findByUserIdOrderByLogDateDesc(Long userId);

    @Query("SELECT h FROM HealthLog h WHERE h.user.id = :userId AND h.logDate BETWEEN :startDate AND :endDate ORDER BY h.logDate DESC")
    List<HealthLog> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT AVG(h.stressLevel) FROM HealthLog h WHERE h.user.id = :userId AND h.logDate >= :since")
    Double getAverageStressLevel(@Param("userId") Long userId,
                                 @Param("since") LocalDate since);
}

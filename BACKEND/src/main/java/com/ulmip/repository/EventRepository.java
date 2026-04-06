package com.ulmip.repository;

import com.ulmip.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserIdOrderByStartTimeAsc(Long userId);

    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.startTime BETWEEN :start AND :end ORDER BY e.startTime ASC")
    List<Event> findByUserIdAndStartTimeBetween(@Param("userId") Long userId,
                                                @Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end);
}

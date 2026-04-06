package com.ulmip.repository;

import com.ulmip.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByDateAscTimeAsc();
    List<Event> findByDateBetweenOrderByDateAscTimeAsc(LocalDate start, LocalDate end);
    List<Event> findByDateGreaterThanEqualOrderByDateAscTimeAsc(LocalDate date);
}

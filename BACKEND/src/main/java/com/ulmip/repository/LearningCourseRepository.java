package com.ulmip.repository;

import com.ulmip.model.LearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningCourseRepository extends JpaRepository<LearningCourse, Long> {

    List<LearningCourse> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<LearningCourse> findByUserIdAndStatus(Long userId, LearningCourse.Status status);

    @Query("SELECT SUM(l.spentHours) FROM LearningCourse l WHERE l.user.id = :userId")
    Double getTotalLearningHours(@Param("userId") Long userId);
}

package com.ulmip.repository;

import com.ulmip.model.AiSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiSuggestionRepository extends JpaRepository<AiSuggestion, Long> {

    List<AiSuggestion> findByUserIdAndIsReadFalseOrderByPriorityDesc(Long userId);

    List<AiSuggestion> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT COUNT(s) FROM AiSuggestion s WHERE s.user.id = :userId AND s.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);
}

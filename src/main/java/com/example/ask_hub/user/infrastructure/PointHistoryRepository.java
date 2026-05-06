package com.example.ask_hub.user.infrastructure;

import com.example.ask_hub.user.domain.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Query("SELECT ph FROM PointHistory ph WHERE ph.user.id = :userId AND ph.createdAt >= :oneWeekAgo ORDER BY ph.createdAt")
    List<PointHistory> findRecentByUserId(@Param("userId") Long userId, @Param("oneWeekAgo") LocalDateTime oneWeekAgo);

}

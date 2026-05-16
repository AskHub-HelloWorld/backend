package com.example.ask_hub.team.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ask_hub.team.domain.entity.Session;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByTeamId(Long teamId);

    Slice<Session> findAllByUserId(Long userId, Pageable pageable);

    @Query("SELECT s FROM Session s " +
            "JOIN s.team t " +
            "WHERE s.user.id = :userId " +
            "AND t.name LIKE %:keyword%")
    Slice<Session> findByUserIdAndTeamName(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
}

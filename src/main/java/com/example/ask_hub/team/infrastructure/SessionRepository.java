package com.example.ask_hub.team.infrastructure;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ask_hub.team.domain.entity.Session;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByTeamId(Long teamId);

    Slice<Session> findAllByUserId(Long userId, Pageable pageable);
}

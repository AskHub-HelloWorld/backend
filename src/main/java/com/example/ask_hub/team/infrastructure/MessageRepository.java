package com.example.ask_hub.team.infrastructure;

import com.example.ask_hub.team.domain.entity.Message;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findTopBySessionIdOrderByCreatedAtDesc(Long sessionId);

    void deleteAllBySessionId(Long sessionId);

    Slice<Message> findAllBySessionIdOrderByCreatedAt(Long sessionId);
}

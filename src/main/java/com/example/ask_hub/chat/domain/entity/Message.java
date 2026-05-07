package com.example.ask_hub.chat.domain.entity;

import com.example.ask_hub.chat.domain.enums.Role;
import com.example.ask_hub.team.domain.entity.Session;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_team_id", nullable = false)
    private Session session;

    @Column(nullable = false, columnDefinition = "text")
    private String context;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Message(Session session, String context, Role role) {
        this.session = session;
        this.context = context;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }
}
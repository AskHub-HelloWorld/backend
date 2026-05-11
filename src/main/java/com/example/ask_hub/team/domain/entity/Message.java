package com.example.ask_hub.team.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.team.domain.enums.Role;
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
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_team_id", nullable = false)
    private Session session;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, length = 50)
    private String uuid;

    @Builder
    public Message(Session session, String content, Role role, String uuid) {
        this.session = session;
        this.content = content;
        this.role = role;
        this.uuid = uuid;
    }
}
package com.example.ask_hub.user.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.user.domain.enums.Position;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Position position;

    @Column
    private Integer year; // 연차

    @Column
    private Integer point;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public User(String email, String password, String name, Position position, Integer year) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.position = position;
        this.year = year;
        this.point = 0;
    }
}
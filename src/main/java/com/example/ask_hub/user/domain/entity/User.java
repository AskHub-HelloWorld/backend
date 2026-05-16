package com.example.ask_hub.user.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.user.domain.enums.Position;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = false, length = 30)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Position position;

    @Column(nullable = false)
    private LocalDate joinedDate; // 입사일자

    @Column
    private Integer point;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public User(String email, String password, String name, String company, Position position, LocalDate joinedDate) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.company = company;
        this.position = position;
        this.joinedDate = joinedDate;
        this.point = 0;
    }

    public void signout() {
        deletedAt = LocalDateTime.now();
    }

    public void restore() {
        deletedAt = null;
    }

    public void modifyInfo(String name, String company, Position position, String email, LocalDate joinedDate) {
        this.name = name;
        this.company = company;
        this.position = position;
        this.email = email;
        this.joinedDate = joinedDate;
    }
}
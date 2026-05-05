package com.example.ask_hub.convention.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.team.domain.entity.Team;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "convention")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Convention extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "convention_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "file_url", nullable = false, length = 30)
    private String fileUrl;

    @Builder
    public Convention(Team team, String fileUrl) {
        this.team = team;
        this.fileUrl = fileUrl;
    }
}

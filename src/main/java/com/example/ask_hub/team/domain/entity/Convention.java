package com.example.ask_hub.team.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
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

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "source_id", length = 200)
    private String sourceId;

    @Column(name = "file_url" , length = 250)
    private String fileUrl;

    @Builder
    public Convention(Team team, String name, String sourceId, String fileUrl) {
        this.team = team;
        this.sourceId = sourceId;
        this.fileUrl = fileUrl;
        this.name = name;
    }
}

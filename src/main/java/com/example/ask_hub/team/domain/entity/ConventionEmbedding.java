package com.example.ask_hub.team.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "convention_embedding")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConventionEmbedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "embedding_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convention_id", nullable = false)
    private Convention convention;

    @Column(name = "chunk_text", nullable = false, columnDefinition = "text")
    private String chunkText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public ConventionEmbedding(Convention convention, String chunkText) {
        this.convention = convention;
        this.chunkText = chunkText;
        this.createdAt = LocalDateTime.now();
    }
}
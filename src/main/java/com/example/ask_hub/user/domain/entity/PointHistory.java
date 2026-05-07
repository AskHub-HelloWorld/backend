package com.example.ask_hub.user.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.post.domain.entity.Post;
import com.example.ask_hub.user.domain.enums.Purpose;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long pointHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Integer point; // current point

    @Enumerated(EnumType.STRING)
    private Purpose purpose;
}
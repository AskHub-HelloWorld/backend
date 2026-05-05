package com.example.ask_hub.user.domain.entity;

import com.example.ask_hub.global.domain.BaseEntity;
import com.example.ask_hub.user.domain.enums.VerificationPurpose;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Verification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationPurpose purpose;

    @Column
    private Integer count;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public Verification(String email, String code, VerificationPurpose purpose) {
        this.email = email;
        this.code = code;
        this.purpose = purpose;
        this.count = 0;
        this.expiresAt = LocalDateTime.now().plusMinutes(5);
    }
}

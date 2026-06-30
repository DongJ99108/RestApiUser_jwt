package com.example.restapiuser.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "REFRESH_TOKEN",
        indexes = {
                @Index(name = "IDX_REFRESH_TOKEN_HASH", columnList = "TOKEN_HASH", unique = true),
                @Index(name = "IDX_REFRESH_TOKEN_USERID", columnList = "USERID")
        }
)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USERID", nullable = false)
    private UserEntity user;

    // DB 탈취 시 원문 refresh token이 바로 노출되지 않도록 SHA-256 해시만 저장합니다.
    @Column(name = "TOKEN_HASH", length = 64, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "REVOKED", nullable = false)
    private boolean revoked = false;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected RefreshTokenEntity() {
    }

    public RefreshTokenEntity(UserEntity user, String tokenHash, LocalDateTime expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void revoke() {
        this.revoked = true;
    }
}

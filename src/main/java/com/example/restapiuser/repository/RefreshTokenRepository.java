package com.example.restapiuser.repository;

import com.example.restapiuser.entity.RefreshTokenEntity;
import com.example.restapiuser.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findByUserAndRevokedFalse(UserEntity user);

    void deleteByExpiresAtBefore(LocalDateTime now);

    void deleteByUser(UserEntity user);
}

package com.example.restapiuser.service;

import com.example.restapiuser.config.JwtProperties;
import com.example.restapiuser.entity.RefreshTokenEntity;
import com.example.restapiuser.entity.UserEntity;
import com.example.restapiuser.exception.ApiException;
import com.example.restapiuser.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    public String createRefreshToken(UserEntity user) {
        revokeAllActiveTokens(user);

        String rawToken = createRandomToken();
        RefreshTokenEntity refreshToken = new RefreshTokenEntity(
                user,
                sha256(rawToken),
                LocalDateTime.now().plusDays(jwtProperties.refreshTokenDays())
        );
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    @Transactional(readOnly = true)
    public UserEntity verifyAndGetUser(String rawToken) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenHash(sha256(rawToken))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다"));

        if (refreshToken.isRevoked()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이미 폐기된 Refresh Token입니다");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "만료된 Refresh Token입니다");
        }

        if (!refreshToken.getUser().isEnabled()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "비활성화된 사용자입니다");
        }

        return refreshToken.getUser();
    }

    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(sha256(rawToken))
                .ifPresent(RefreshTokenEntity::revoke);
    }

    public void revokeAllActiveTokens(UserEntity user) {
        refreshTokenRepository.findByUserAndRevokedFalse(user)
                .forEach(RefreshTokenEntity::revoke);
    }

    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String createRandomToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다", e);
        }
    }
}

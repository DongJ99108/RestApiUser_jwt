package com.example.restapiuser.service;

import com.example.restapiuser.dto.AuthResponse;
import com.example.restapiuser.dto.LoginRequest;
import com.example.restapiuser.dto.TokenRefreshRequest;
import com.example.restapiuser.dto.UserResponse;
import com.example.restapiuser.entity.UserEntity;
import com.example.restapiuser.exception.ApiException;
import com.example.restapiuser.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse login(LoginRequest request) {
        // Spring security 내부의 AuthenticationManager 기능을 호출
        // 기본 security 로그인 로직 자동으로 작동된다
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userid(), request.passwd())
        );
        // security/CustomUserDetailsService.java loadUserByUsername() 으로 db 조회
        // 내부흐름
        // AuthService
        //     ↓
        // AuthenticationManager
        //     ↓
        // DaoAuthenticationProvider : 자동 비교 UsernamePasswordAuthenticationToken == UserDetails
        //     ↓
        // CustomUserDetailsService : 수동 loadUserByUsername()
        //     ↓
        // userRepository
        //     ↓
        // PasswordEncoder.matches() : 자동
        // 로그인 처리완료 상태

        // 인증 성공 후 사용자 조회
        UserEntity user = userRepository.findById(request.userid())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "로그인 정보가 올바르지 않습니다"));

        return issueTokens(user);
    }

    public AuthResponse refresh(TokenRefreshRequest request) {
        UserEntity user = refreshTokenService.verifyAndGetUser(request.refreshToken());
        refreshTokenService.revoke(request.refreshToken());
        return issueTokens(user);
    }

    public void logout(TokenRefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    private AuthResponse issueTokens(UserEntity user) {
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.bearer(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpiresInSeconds(),
                UserResponse.from(user)
        );
    }
}

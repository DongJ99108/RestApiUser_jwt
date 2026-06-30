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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userid(), request.passwd())
        );

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

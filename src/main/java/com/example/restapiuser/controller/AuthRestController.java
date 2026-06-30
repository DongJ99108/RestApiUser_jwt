package com.example.restapiuser.controller;

import com.example.restapiuser.dto.AuthResponse;
import com.example.restapiuser.dto.LoginRequest;
import com.example.restapiuser.dto.MessageResponse;
import com.example.restapiuser.dto.TokenRefreshRequest;
import com.example.restapiuser.dto.UserResponse;
import com.example.restapiuser.service.AuthService;
import com.example.restapiuser.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;
    private final UserService userService;

    public AuthRestController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refresh(request);
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public MessageResponse logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request);
        return new MessageResponse("로그아웃되었습니다");
    }

    // GET /api/auth/me
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.findUser(authentication.getName());
    }
}

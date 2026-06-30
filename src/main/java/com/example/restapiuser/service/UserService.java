package com.example.restapiuser.service;

import com.example.restapiuser.dto.UserCreateRequest;
import com.example.restapiuser.dto.UserResponse;
import com.example.restapiuser.dto.UserUpdateRequest;
import com.example.restapiuser.entity.Role;
import com.example.restapiuser.entity.UserEntity;
import com.example.restapiuser.exception.ApiException;
import com.example.restapiuser.repository.RefreshTokenRepository;
import com.example.restapiuser.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // UserEntity(table) -> UserResponse(화면 출력용, 비밀번호 제외)
    @Transactional(readOnly = true)
    public List<UserResponse> findUsers(String keyword) {

        List<UserEntity> users;
        if( keyword == null || keyword.isBlank() ) {
            users = userRepository.findAllByOrderByIndateAsc();
        } else {
            users = userRepository
                    .findByUseridContainingIgnoreCaseOrderByIndateAsc(keyword.trim());
        }
        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findUser(String userid) {
        UserEntity user = findEntity(userid);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse createUser(@Valid UserCreateRequest request) {
        if( userRepository.existsById( request.userid() ) ) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "이미 존재하는 아이디입니다: " + request.userid() );
        }

        UserEntity user = new UserEntity(
                request.userid(),
                passwordEncoder.encode(request.passwd()),
                request.username(),
                request.email(),
                Role.USER
        );

        UserEntity savedUser = userRepository.save(user);
        return UserResponse.from(savedUser);
    }

    @Transactional
    public UserResponse updateUser(String userid, @Valid UserUpdateRequest request) {
        UserEntity user = findEntity(userid);

        if (request.passwd() != null && !request.passwd().isBlank()) {
            user.setPasswd(passwordEncoder.encode(request.passwd()));
        }
        if (request.username() != null && !request.username().isBlank()) {
            user.setUsername(request.username());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }

        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(String userid) {
        UserEntity user = findEntity(userid);
        refreshTokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    private UserEntity findEntity(String userid) {
        return userRepository.findById(userid)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "사용자를 찾을 수 없습니다: " + userid));
    }
}

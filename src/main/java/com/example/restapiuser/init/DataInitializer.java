package com.example.restapiuser.init;

import com.example.restapiuser.entity.Role;
import com.example.restapiuser.entity.UserEntity;
import com.example.restapiuser.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements ApplicationRunner {
    // ApplicationRunner : 애플리케이션이 시작된 후 자동으로 실행되는 run() 메서드를 가진 클래스
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createIfNotExists("admin", "admin1234", "관리자", "admin@example.com", Role.ADMIN);
        createIfNotExists("user1", "user1234", "홍길동", "user1@example.com", Role.USER);
        createIfNotExists("user2", "user1234", "김자바", "user2@example.com", Role.USER);
        createIfNotExists("oracle", "oracle1234", "오라클", "oracle@example.com", Role.USER);
        createIfNotExists("restapi", "rest1234", "REST API", "restapi@example.com", Role.USER);
    }

    private void createIfNotExists(String userid, String passwd, String username, String email, Role role) {
        userRepository.findById(userid).ifPresentOrElse(user -> {
            // 기존 예제를 실행한 DB에는 비밀번호가 평문으로 남아 있을 수 있습니다.
            // 기본 계정은 시작 시 BCrypt 비밀번호와 권한 값으로 보정합니다.
            if (!user.getPasswd().startsWith("$2")) {
                user.setPasswd(passwordEncoder.encode(passwd));
            }
            user.setRole(role);
            user.setEnabled(true);
        }, () -> userRepository.save(
                new UserEntity(userid, passwordEncoder.encode(passwd), username, email, role)
        ));
    }
}

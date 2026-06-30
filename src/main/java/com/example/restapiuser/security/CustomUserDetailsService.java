package com.example.restapiuser.security;

import com.example.restapiuser.entity.UserEntity;
import com.example.restapiuser.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(userid)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다: " + userid));

        return User.withUsername(user.getUserid())
                .password(user.getPasswd())
                .disabled(!user.isEnabled())
                .roles(user.getRole().name())
                .build();
    }
}

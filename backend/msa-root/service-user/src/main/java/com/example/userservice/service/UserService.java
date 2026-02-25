package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserDto register(CreateUserRequest req) {
        // 기본 검증: 동일 이메일이 이미 존재하면 예외
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호를 해시처리
        String hashed = passwordEncoder.encode(req.password());
        // create entity
        User user = new User(null, req.email(), hashed, req.name(), "USER", Instant.now());
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getEmail(), saved.getName(), saved.getRoles(), saved.getCreatedAt());
    }

    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRoles(), u.getCreatedAt()))
                .orElse(null);
    }
}

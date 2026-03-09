package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateProfileRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import java.time.Instant;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/*
 * Service that handles user management operations such as registration, profile
 * retrieval and updates.
 */
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserDto register(CreateUserRequest req) {
    // 기본 검증: 동일 이메일이 이미 존재하면 예외
    if (userRepository.findByEmail(req.getEmail()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 비밀번호를 해시처리
    String hashed = passwordEncoder.encode(req.getPassword());
    // create entity using builder
    User user = User.builder()
        .id(java.util.UUID.randomUUID().toString())
        .email(req.getEmail())
        .passwordHash(hashed)
        .name(req.getName())
        .phone(null)
        .roles("USER")
        .provider("local")
        .providerId(null)
        .createdAt(Instant.now())
        .isNewEntity(true)
        .build();

    User saved = userRepository.save(user);
    return new UserDto(
        saved.getId(), saved.getEmail(), saved.getName(), saved.getRoles(), saved.getCreatedAt());
  }

  public UserDto findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRoles(), u.getCreatedAt()))
        .orElse(null);
  }

  public UserDto findById(String id) {
    return userRepository
        .findById(id)
        .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRoles(), u.getCreatedAt()))
        .orElse(null);
  }

  public UserProfileDto getProfile(String id) {
    User user = userRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    return new UserProfileDto(user.getId(), user.getEmail(), user.getName(), user.getPhone());
  }

  @Transactional
  public UserProfileDto updateProfile(String id, UpdateProfileRequest req) {
    User user = userRepository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    // use toBuilder() to create a modified instance
    User updated = user.toBuilder().name(req.getName()).phone(req.getPhone()).build();
    User saved = userRepository.save(updated);
    return new UserProfileDto(saved.getId(), saved.getEmail(), saved.getName(), saved.getPhone());
  }

  // 이메일/비밀번호 인증 (service-auth에서 호출 가능)
  public com.example.userservice.dto.AuthUserDto authenticate(String email, String rawPassword) {
    return userRepository
        .findByEmail(email)
        .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
        .map(
            u -> new com.example.userservice.dto.AuthUserDto(
                u.getId(), u.getEmail(), u.getName(), u.getRoles()))
        .orElse(null);
  }

  @Transactional
  public void withdraw(String id) {
    // Delete user and cascade addresses if needed
    userRepository.deleteById(id);
  }
}

package com.example.userservice.service;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.dto.UpdateProfileRequest;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserProfileDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** UserService 타입입니다. */
@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final Path avatarUploadPath;
  private final String avatarPublicBaseUrl;

  /** 사용자 서비스를 생성합니다. */
  public UserService(
      UserRepository userRepository,
      BCryptPasswordEncoder passwordEncoder,
      @Value("${app.avatar.upload-dir:uploads/avatars}") String avatarUploadDir,
      @Value("${app.avatar.public-base-url:/api/v1/users/avatars/}") String avatarPublicBaseUrl) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.avatarUploadPath = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
    this.avatarPublicBaseUrl =
        avatarPublicBaseUrl.endsWith("/") ? avatarPublicBaseUrl : avatarPublicBaseUrl + "/";
  }

  /** 사용자를 등록합니다. */
  @Transactional
  public UserDto register(CreateUserRequest req) {
    // 기본 검증: 동일 이메일이 이미 존재하면 예외
    if (userRepository.findByEmail(req.getEmail()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 비밀번호를 해시처리
    String hashed = passwordEncoder.encode(req.getPassword());
    // create entity using builder
    User user =
        User.builder()
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

  /** 이메일로 사용자를 조회합니다. */
  public UserDto findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRoles(), u.getCreatedAt()))
        .orElse(null);
  }

  /** ID로 사용자를 조회합니다. */
  public UserDto findById(String id) {
    return userRepository
        .findById(id)
        .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRoles(), u.getCreatedAt()))
        .orElse(null);
  }

  /** 사용자 프로필을 조회합니다. */
  public UserProfileDto getProfile(String id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    return toUserProfileDto(user);
  }

  /** 사용자 프로필을 수정합니다. */
  @Transactional
  public UserProfileDto updateProfile(String id, UpdateProfileRequest req) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    // use toBuilder() to create a modified instance
    User updated =
        user.toBuilder()
            .name(req.getName())
            .username(req.getUsername())
            .phone(req.getPhone())
            .avatarUrl(req.getAvatarUrl())
            .location(req.getLocation())
            .build();
    User saved = userRepository.save(updated);
    return toUserProfileDto(saved);
  }

  /** 사용자 아바타를 업로드하고 프로필을 갱신합니다. */
  @Transactional
  public UserProfileDto updateAvatar(String id, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("업로드할 이미지 파일이 필요합니다.");
    }
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
    }

    String extension = resolveExtension(file.getOriginalFilename());
    String fileName = UUID.randomUUID() + extension;
    Path target = avatarUploadPath.resolve(fileName).normalize();
    if (!target.startsWith(avatarUploadPath)) {
      throw new IllegalArgumentException("잘못된 파일 경로입니다.");
    }

    try {
      Files.createDirectories(avatarUploadPath);
      try (InputStream inputStream = file.getInputStream()) {
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new RuntimeException("아바타 파일 저장 중 오류가 발생했습니다.", e);
    }

    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

    String previousAvatarUrl = user.getAvatarUrl();
    User saved =
        userRepository.save(user.toBuilder().avatarUrl(avatarPublicBaseUrl + fileName).build());
    deleteLocalAvatarIfManaged(previousAvatarUrl);
    return toUserProfileDto(saved);
  }

  /** 저장된 아바타 리소스를 반환합니다. */
  public Resource loadAvatar(String filename) {
    String safeFilename = Paths.get(filename).getFileName().toString();
    Path target = avatarUploadPath.resolve(safeFilename).normalize();
    if (!target.startsWith(avatarUploadPath) || !Files.exists(target)) {
      throw new NoSuchElementException("아바타 파일을 찾을 수 없습니다.");
    }

    try {
      return new UrlResource(target.toUri());
    } catch (IOException e) {
      throw new RuntimeException("아바타 파일 조회 중 오류가 발생했습니다.", e);
    }
  }

  /** 이메일과 비밀번호로 사용자를 인증합니다. */
  public com.example.userservice.dto.AuthUserDto authenticate(String email, String rawPassword) {
    return userRepository
        .findByEmail(email)
        .filter(u -> passwordEncoder.matches(rawPassword, u.getPasswordHash()))
        .map(
            u ->
                new com.example.userservice.dto.AuthUserDto(
                    u.getId(), u.getEmail(), u.getName(), u.getRoles()))
        .orElse(null);
  }

  /** 사용자를 삭제합니다. */
  @Transactional
  public void withdraw(String id) {
    // Delete user and cascade addresses if needed
    userRepository.deleteById(id);
  }

  private UserProfileDto toUserProfileDto(User user) {
    return new UserProfileDto(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getUsername(),
        user.getPhone(),
        user.getAvatarUrl(),
        user.getLocation());
  }

  private void deleteLocalAvatarIfManaged(String avatarUrl) {
    if (avatarUrl == null || !avatarUrl.startsWith(avatarPublicBaseUrl)) {
      return;
    }
    String filename = avatarUrl.substring(avatarPublicBaseUrl.length());
    if (filename.isBlank()) {
      return;
    }
    Path oldTarget =
        avatarUploadPath.resolve(Paths.get(filename).getFileName().toString()).normalize();
    if (!oldTarget.startsWith(avatarUploadPath)) {
      return;
    }
    try {
      Files.deleteIfExists(oldTarget);
    } catch (IOException ignored) {
      // 이전 파일 삭제 실패는 사용자 플로우를 막지 않도록 무시
    }
  }

  private String resolveExtension(String originalFilename) {
    if (originalFilename == null) {
      return "";
    }
    int dot = originalFilename.lastIndexOf('.');
    if (dot < 0 || dot == originalFilename.length() - 1) {
      return "";
    }
    String ext = originalFilename.substring(dot).toLowerCase();
    if (ext.length() > 8) {
      return "";
    }
    return ext;
  }
}

package com.example.userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.userservice.dto.CreateUserRequest;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private BCryptPasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, passwordEncoder, "build/test-avatars", "/api/v1/users/avatars/");
  }

  @Test
  void registerCreatesUserWithEncodedPassword() {
    CreateUserRequest request = new CreateUserRequest("user@example.com", "password123", "Tester");

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));

    var result = userService.register(request);

    assertThat(result.getEmail()).isEqualTo("user@example.com");
    assertThat(result.getName()).isEqualTo("Tester");
    assertThat(result.getRoles()).isEqualTo("USER");

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue().getPasswordHash()).isEqualTo("encoded-password");
    assertThat(captor.getValue().getCreatedAt()).isNotNull();
  }

  @Test
  void registerRejectsDuplicateEmail() {
    CreateUserRequest request = new CreateUserRequest("user@example.com", "password123", "Tester");

    when(userRepository.findByEmail("user@example.com"))
        .thenReturn(
            Optional.of(
                User.builder()
                    .id("user-1")
                    .email("user@example.com")
                    .passwordHash("encoded")
                    .name("Existing")
                    .roles("USER")
                    .provider("local")
                    .createdAt(Instant.now())
                    .isNewEntity(false)
                    .build()));

    assertThatThrownBy(() -> userService.register(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("이미 존재하는 이메일");
  }

  @Test
  void authenticateReturnsUserWhenPasswordMatches() {
    User user =
        User.builder()
            .id("user-1")
            .email("user@example.com")
            .passwordHash("encoded-password")
            .name("Tester")
            .roles("ADMIN")
            .provider("local")
            .createdAt(Instant.now())
            .isNewEntity(false)
            .build();

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);

    var result = userService.authenticate("user@example.com", "password123");

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("user-1");
    assertThat(result.getRoles()).isEqualTo("ADMIN");
  }

  @Test
  void authenticateReturnsNullWhenPasswordDoesNotMatch() {
    User user =
        User.builder()
            .id("user-1")
            .email("user@example.com")
            .passwordHash("encoded-password")
            .name("Tester")
            .roles("USER")
            .provider("local")
            .createdAt(Instant.now())
            .isNewEntity(false)
            .build();

    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

    assertThat(userService.authenticate("user@example.com", "wrong-password")).isNull();
  }
}

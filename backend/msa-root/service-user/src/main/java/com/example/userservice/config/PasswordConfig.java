package com.example.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** PasswordConfig 타입입니다. */
@Configuration
public class PasswordConfig {

  /** 비밀번호 해싱을 위한 BCrypt 인코더 빈을 제공합니다. */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

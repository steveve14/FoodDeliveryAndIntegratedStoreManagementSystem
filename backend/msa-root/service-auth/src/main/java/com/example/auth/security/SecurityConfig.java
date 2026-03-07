package com.example.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정.
 *
 * <p>service-auth는 직접 JWT를 발급하는 서비스이므로 자체 엔드포인트(/api/v1/auth/**)는 인증 없이 접근을 허용해야 합니다. 게이트웨이가 이미 토큰
 * 검증을 담당하고 있으므로 여기서는 CSRF / formLogin / httpBasic 을 모두 비활성화합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // CSRF 비활성화 — REST API 전용 서비스
        .csrf(csrf -> csrf.disable())
        // 세션 사용하지 않음 — Stateless JWT 방식
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // 폼 로그인 비활성화 — REST API이므로 로그인 페이지 불필요
        .formLogin(form -> form.disable())
        // HTTP Basic 비활성화
        .httpBasic(basic -> basic.disable())
        // 엔드포인트 인가 설정
        .authorizeHttpRequests(
            auth ->
                auth
                    // 인증 관련 API는 모두 허용
                    .requestMatchers("/api/v1/auth/**")
                    .permitAll()
                    // 헬스체크 / 정보
                    .requestMatchers("/api/v1/info")
                    .permitAll()
                    // gRPC 포트는 별도이므로 여기서는 무관
                    // 그 외 요청은 인증 필요
                    .anyRequest()
                    .authenticated());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

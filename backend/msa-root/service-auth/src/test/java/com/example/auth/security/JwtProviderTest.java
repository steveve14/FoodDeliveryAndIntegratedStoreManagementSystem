package com.example.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtProviderTest {

  private JwtProvider jwtProvider;

  @BeforeEach
  void setUp() throws Exception {
    jwtProvider = new JwtProvider();
    setField("tokenSecret", "c2lsdmVyLTI1Ni1iaXQtc2VjcmV0LWtleS1mb3ItjwtlbmNvZGluZy1leGFtcGxlCg==");
    setField("accessTokenValidityMs", 3600000L);
    setField("refreshTokenValidityMs", 1209600000L);
    jwtProvider.init();
  }

  @Test
  void createAccessTokenAndExtractClaims() {
    String token = jwtProvider.createAccessToken("user-1", "ADMIN");

    assertThat(jwtProvider.validateToken(token)).isTrue();
    assertThat(jwtProvider.getUserIdFromToken(token)).isEqualTo("user-1");
    assertThat(jwtProvider.getRolesFromToken(token)).isEqualTo("ADMIN");
  }

  @Test
  void createRefreshTokenIsAlsoValidAndContainsRole() {
    String token = jwtProvider.createRefreshToken("user-2", "USER");

    assertThat(jwtProvider.validateToken(token)).isTrue();
    assertThat(jwtProvider.getUserIdFromToken(token)).isEqualTo("user-2");
    assertThat(jwtProvider.getRolesFromToken(token)).isEqualTo("USER");
  }

  @Test
  void validateTokenReturnsFalseForTamperedToken() {
    String token = jwtProvider.createAccessToken("user-3", "STORE");
    String tamperedToken = token.substring(0, token.length() - 2) + "ab";

    assertThat(jwtProvider.validateToken(tamperedToken)).isFalse();
  }

  private void setField(String fieldName, Object value) throws Exception {
    Field field = JwtProvider.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(jwtProvider, value);
  }
}

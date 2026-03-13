package com.example.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class AuthorizationServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = AuthorizationServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

package com.example.userservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/** UserServiceApplicationTest 타입입니다. */
class UserServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = UserServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

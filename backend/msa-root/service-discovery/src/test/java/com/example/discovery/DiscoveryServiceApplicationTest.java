package com.example.discovery;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/** DiscoveryServiceApplicationTest 타입입니다. */
class DiscoveryServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = DiscoveryServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

package com.example.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/** OrderServiceApplicationTest 타입입니다. */
class OrderServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = OrderServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

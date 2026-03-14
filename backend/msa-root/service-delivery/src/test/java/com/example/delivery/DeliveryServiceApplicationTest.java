package com.example.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/** DeliveryServiceApplicationTest 타입입니다. */
class DeliveryServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = DeliveryServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

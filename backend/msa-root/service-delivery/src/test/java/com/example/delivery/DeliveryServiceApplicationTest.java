package com.example.delivery;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class DeliveryServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = DeliveryServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

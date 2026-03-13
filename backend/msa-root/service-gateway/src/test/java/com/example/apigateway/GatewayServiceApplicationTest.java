package com.example.apigateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class GatewayServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = GatewayServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

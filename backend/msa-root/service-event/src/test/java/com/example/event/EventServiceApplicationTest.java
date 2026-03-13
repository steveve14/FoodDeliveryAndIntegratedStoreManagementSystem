package com.example.event;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class EventServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = EventServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

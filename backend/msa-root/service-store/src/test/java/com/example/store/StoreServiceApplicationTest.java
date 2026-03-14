package com.example.store;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/** StoreServiceApplicationTest 타입입니다. */
class StoreServiceApplicationTest {

  @Test
  void exposesMainMethod() throws Exception {
    Method mainMethod = StoreServiceApplication.class.getMethod("main", String[].class);

    assertThat(mainMethod).isNotNull();
  }
}

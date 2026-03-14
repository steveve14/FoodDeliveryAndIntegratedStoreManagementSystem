package com.example.apigateway.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.apigateway.filter.AuthorizationHeaderFilter;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

/** GatewayRouteConfigTest 타입입니다. */
class GatewayRouteConfigTest {

  @Test
  void gatewayRouteConfigCanBeConstructedWithAuthFilter() {
    Environment environment = mock(Environment.class);
    when(environment.getProperty("token.secret"))
        .thenReturn("c2lsdmVyLTI1Ni1iaXQtc2VjcmV0LWtleS1mb3ItjwtlbmNvZGluZy1leGFtcGxlCg==");

    AuthorizationHeaderFilter authFilter = new AuthorizationHeaderFilter(environment);
    GatewayRouteConfig gatewayRouteConfig = new GatewayRouteConfig(authFilter);

    assertThat(gatewayRouteConfig).isNotNull();
    assertThat(authFilter.apply(new AuthorizationHeaderFilter.Config())).isNotNull();
  }
}

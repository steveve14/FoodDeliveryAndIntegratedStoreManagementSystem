package com.example.apigateway.config;

import com.example.apigateway.filter.AuthorizationHeaderFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 게이트웨이 라우트를 Java 코드로 정의합니다. */
/** GatewayRouteConfig 타입입니다. */
@Configuration
public class GatewayRouteConfig {

  private final AuthorizationHeaderFilter authFilter;

  public GatewayRouteConfig(AuthorizationHeaderFilter authFilter) {
    this.authFilter = authFilter;
  }

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    GatewayFilter authGatewayFilter = authFilter.apply(new AuthorizationHeaderFilter.Config());

    return builder
        .routes()
        // 1. 인증/보안 서비스 — 인증 필터 미적용
        .route("service-auth", r -> r.path("/api/v1/auth/**").uri("lb://SERVICE-AUTH"))
        // 2-a. 회원가입 — 인증 필터 미적용
        .route(
            "service-user-register", r -> r.path("/api/v1/users/register").uri("lb://SERVICE-USER"))
        // 2-b. 회원 서비스 (인증 필요, 세부 인가는 서비스에서 처리)
        .route(
            "service-user",
            r ->
                r.path("/api/v1/users/**")
                    .filters(f -> f.filter(authGatewayFilter))
                    .uri("lb://SERVICE-USER"))
        // 3. 가게 서비스 (인증 필요, 점주/관리자 검증은 서비스에서 처리)
        .route(
            "service-store-public",
            r ->
                r.method("GET")
                    .and()
                    .path("/api/v1/stores", "/api/v1/stores/*", "/api/v1/stores/*/menus")
                    .uri("lb://SERVICE-STORE"))
        // 3-b. 가게 관리 API는 인증 필요
        .route(
            "service-store-protected",
            r ->
                r.path("/api/v1/stores/**")
                    .filters(f -> f.filter(authGatewayFilter))
                    .uri("lb://SERVICE-STORE"))
        // 4. 주문 서비스
        .route(
            "service-order",
            r ->
                r.path("/api/v1/orders/**")
                    .filters(f -> f.filter(authGatewayFilter))
                    .uri("lb://SERVICE-ORDER"))
        // 5. 배달 서비스
        .route(
            "service-delivery",
            r ->
                r.path("/api/v1/deliveries/**")
                    .filters(f -> f.filter(authGatewayFilter))
                    .uri("lb://SERVICE-DELIVERY"))
        // 6. 이벤트 서비스
        .route(
            "service-event",
            r ->
                r.path("/api/v1/events/**")
                    .filters(f -> f.filter(authGatewayFilter))
                    .uri("lb://SERVICE-EVENT"))
        .build();
  }
}

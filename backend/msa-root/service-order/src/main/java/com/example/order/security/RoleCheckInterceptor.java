package com.example.order.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/** Gateway가 전달하는 X-User-Role 헤더를 읽어 @RequireRole 어노테이션과 비교하는 인터셉터 */
/** RoleCheckInterceptor 타입입니다. */
@Slf4j
@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    if (!(handler instanceof HandlerMethod handlerMethod)) {
      return true;
    }

    // 메서드 → 클래스 순서로 @RequireRole 탐색
    RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
    if (requireRole == null) {
      requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }
    if (requireRole == null) {
      return true; // 역할 제한 없음
    }

    String userRole = request.getHeader("X-User-Role");
    if (userRole == null || userRole.isBlank()) {
      log.warn("Missing X-User-Role header for {}", request.getRequestURI());
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.getWriter().write("{\"success\":false,\"error\":\"역할 정보가 없습니다.\"}");
      return false;
    }

    boolean allowed = Arrays.asList(requireRole.value()).contains(userRole);
    if (!allowed) {
      log.warn(
          "Access denied: role '{}' not in {} for {}",
          userRole,
          Arrays.toString(requireRole.value()),
          request.getRequestURI());
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"success\":false,\"error\":\"접근 권한이 없습니다.\"}");
      return false;
    }

    return true;
  }
}

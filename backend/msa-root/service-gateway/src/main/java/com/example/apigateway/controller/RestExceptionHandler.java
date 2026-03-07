package com.example.apigateway.controller;

import com.example.apigateway.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(400, ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiResponse<Object>> handleConflict(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, ex.getMessage()));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleResponseStatus(
      ResponseStatusException ex, ServerWebExchange exchange) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getURI().getPath();
    String method = request.getMethod().name();

    // /api/** 경로가 아닌 요청(favicon.ico 등)의 404는 무시
    if (ex.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND) && !path.startsWith("/api/")) {
      log.debug("404 무시 (비-API 요청): {} {}", method, path);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404, "Not Found"));
    }

    int statusCode = ex.getStatusCode().value();
    log.warn(
        "HTTP {} 응답: {} {} — {}",
        statusCode,
        method,
        path,
        ex.getReason() != null ? ex.getReason() : ex.getMessage());
    return ResponseEntity.status(ex.getStatusCode())
        .body(ApiResponse.error(statusCode, ex.getReason() != null ? ex.getReason() : "Not Found"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
    log.error("예상치 못한 오류 발생: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(500, "서버 오류가 발생했습니다."));
  }
}

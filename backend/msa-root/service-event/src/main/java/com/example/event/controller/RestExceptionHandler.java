package com.example.event.controller;

import com.example.event.dto.ApiResponse;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** RestExceptionHandler 타입입니다. */
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

  /** 유효성 검증 실패 예외를 처리합니다. */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .reduce((a, b) -> a + ", " + b)
            .orElse("입력값이 올바르지 않습니다.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(400, message));
  }

  /** 잘못된 요청 예외를 처리합니다. */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(400, ex.getMessage()));
  }

  /** 리소스 미존재 예외를 처리합니다. */
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ApiResponse<Object>> handleNotFound(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(404, ex.getMessage()));
  }

  /** 상태 충돌 예외를 처리합니다. */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiResponse<Object>> handleConflict(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(409, ex.getMessage()));
  }

  /** 응답 상태 기반 예외를 처리합니다. */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
    String message = ex.getReason() != null ? ex.getReason() : "요청 처리 중 오류가 발생했습니다.";
    return ResponseEntity.status(ex.getStatusCode())
        .body(ApiResponse.error(ex.getStatusCode().value(), message));
  }

  /** 처리되지 않은 예외를 공통으로 처리합니다. */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGeneric(Exception ex) {
    log.error("예상치 못한 오류 발생", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(500, "서버 오류가 발생했습니다."));
  }
}

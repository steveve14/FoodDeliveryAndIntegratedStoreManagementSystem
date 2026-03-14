package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** ApiResponse 타입입니다. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

  private boolean success;
  private int code;
  private String message;
  private T data;

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, 200, "요청이 성공했습니다.", data);
  }

  public static <T> ApiResponse<T> error(int code, String message) {
    return new ApiResponse<>(false, code, message, null);
  }
}

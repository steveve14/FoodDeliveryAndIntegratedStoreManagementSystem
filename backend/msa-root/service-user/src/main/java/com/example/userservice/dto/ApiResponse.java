package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Generic API response wrapper with success flag, data and optional error.
 */
public class ApiResponse<T> {

  private boolean success;
  private T data;
  private String error;

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, data, null);
  }

  public static <T> ApiResponse<T> error(String errorMessage) {
    return new ApiResponse<>(false, null, errorMessage);
  }
}

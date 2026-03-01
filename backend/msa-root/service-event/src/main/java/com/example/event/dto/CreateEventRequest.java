package com.example.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {
  @NotBlank(message = "이벤트 타입을 입력해주세요.")
  private String type;

  private String payload;
}

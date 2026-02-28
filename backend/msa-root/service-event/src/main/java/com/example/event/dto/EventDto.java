package com.example.event.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

  private String id;
  private String type;
  private String payload;
  private Instant createdAt;
}

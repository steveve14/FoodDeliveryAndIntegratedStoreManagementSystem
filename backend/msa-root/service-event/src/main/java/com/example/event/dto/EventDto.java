package com.example.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
	private Long id;
	private String type;
	private String payload;
	private Instant createdAt;
}

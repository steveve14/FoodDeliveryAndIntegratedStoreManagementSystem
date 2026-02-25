package com.example.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
	private Long id;
	private Long orderId;
	private String courier;
	private String status;
	private Instant scheduledAt;
}

package com.example.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayRouteDto {
	private String id;
	private String uri;
	private String predicate;
	private String filter;
}

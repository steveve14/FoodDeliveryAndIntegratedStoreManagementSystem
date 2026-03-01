package com.example.discovery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistryEntry {
  private String serviceId;
  private String host;
  private int port;
  private String status;
}

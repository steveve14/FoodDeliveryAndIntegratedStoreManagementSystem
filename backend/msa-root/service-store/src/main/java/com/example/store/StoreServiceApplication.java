package com.example.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** Application entry point for the store service. */
@SpringBootApplication
@EnableDiscoveryClient
public class StoreServiceApplication {

  /** 애플리케이션 실행 진입점입니다. */
  public static void main(String[] args) {
    SpringApplication.run(StoreServiceApplication.class, args);
  }
}

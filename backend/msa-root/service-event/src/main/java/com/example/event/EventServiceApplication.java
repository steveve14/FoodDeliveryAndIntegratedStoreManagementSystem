package com.example.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** Application entry point for the event service. */
@SpringBootApplication
@EnableDiscoveryClient
public class EventServiceApplication {

  /** 애플리케이션 실행 진입점입니다. */
  public static void main(String[] args) {
    SpringApplication.run(EventServiceApplication.class, args);
  }
}

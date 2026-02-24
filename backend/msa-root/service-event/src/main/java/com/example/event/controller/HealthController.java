package com.example.event.controller;

import com.example.event.dto.InfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
    @GetMapping("/info")
    public InfoResponse info() {
        return InfoResponse.up("service-event");
    }
}

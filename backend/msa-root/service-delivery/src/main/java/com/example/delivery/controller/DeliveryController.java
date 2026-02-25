package com.example.delivery.controller;

import com.example.delivery.dto.CreateDeliveryRequest;
import com.example.delivery.dto.DeliveryDto;
import com.example.delivery.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public ResponseEntity<DeliveryDto> create(@RequestBody CreateDeliveryRequest req) {
        DeliveryDto dto = deliveryService.createDelivery(req.getOrderId(), req.getCourier(), "SCHEDULED");
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryDto> get(@PathVariable Long id) {
        return deliveryService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

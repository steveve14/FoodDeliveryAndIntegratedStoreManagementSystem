package com.example.delivery.service;

import com.example.delivery.dto.DeliveryDto;
import com.example.delivery.entity.Delivery;
import com.example.delivery.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional
    public DeliveryDto createDelivery(Long orderId, String courier, String status) {
        Delivery d = Delivery.builder()
                .id(null)
                .orderId(orderId)
                .courier(courier)
                .status(status)
                .scheduledAt(Instant.now())
                .build();
        Delivery saved = deliveryRepository.save(d);
        return new DeliveryDto(saved.getId(), saved.getOrderId(), saved.getCourier(), saved.getStatus(), saved.getScheduledAt());
    }

    public Optional<DeliveryDto> findById(Long id) {
        return deliveryRepository.findById(id).map(d -> new DeliveryDto(d.getId(), d.getOrderId(), d.getCourier(), d.getStatus(), d.getScheduledAt()));
    }
}

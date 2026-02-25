package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderDto createOrder(Long userId, String status) {
        Order o = Order.builder()
                .id(null)
                .userId(userId)
                .status(status)
                .createdAt(Instant.now())
                .build();
        Order saved = orderRepository.save(o);
        return new OrderDto(saved.getId(), saved.getUserId(), java.util.List.of(), saved.getStatus(), saved.getCreatedAt());
    }

    public Optional<OrderDto> findById(Long id) {
        return orderRepository.findById(id).map(o -> new OrderDto(o.getId(), o.getUserId(), java.util.List.of(), o.getStatus(), o.getCreatedAt()));
    }
}

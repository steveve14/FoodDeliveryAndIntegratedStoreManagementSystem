package com.example.store.dto;

public record ProductDto(Long id, Long storeId, String name, String description, long price) {
}

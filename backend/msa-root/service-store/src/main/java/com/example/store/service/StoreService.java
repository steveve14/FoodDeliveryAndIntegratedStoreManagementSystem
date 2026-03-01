package com.example.store.service;

import com.example.store.dto.StoreDto;
import com.example.store.entity.Store;
import com.example.store.repository.StoreRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/** Service handling store management operations (create, find, list). */
public class StoreService {
  private final StoreRepository storeRepository;

  public StoreService(StoreRepository storeRepository) {
    this.storeRepository = storeRepository;
  }

  @Transactional
  public StoreDto createStore(com.example.store.dto.CreateStoreRequest req) {
    Store s =
        Store.builder()
            .id(java.util.UUID.randomUUID().toString())
            .name(req.getName())
            .address(req.getAddress())
            .phone(req.getPhone())
            .category(req.getCategory())
            .status(req.getStatus())
            .latitude(req.getLatitude())
            .longitude(req.getLongitude())
            .minOrderAmount(req.getMinOrderAmount())
            .ratingAvg(req.getRatingAvg())
            .description(req.getDescription())
            .openingHours(req.getOpeningHours())
            .ownerId(req.getOwnerId())
            .createdAt(Instant.now())
            .build();
    Store saved = storeRepository.save(s);
    return new StoreDto(
        saved.getId(),
        saved.getName(),
        saved.getAddress(),
        saved.getPhone(),
        saved.getCategory(),
        saved.getStatus(),
        saved.getLatitude(),
        saved.getLongitude(),
        saved.getMinOrderAmount(),
        saved.getRatingAvg(),
        saved.getDescription(),
        saved.getOpeningHours(),
        saved.getOwnerId());
  }

  public Optional<StoreDto> findById(String id) {
    return storeRepository
        .findById(id)
        .map(
            s ->
                new StoreDto(
                    s.getId(),
                    s.getName(),
                    s.getAddress(),
                    s.getPhone(),
                    s.getCategory(),
                    s.getStatus(),
                    s.getLatitude(),
                    s.getLongitude(),
                    s.getMinOrderAmount(),
                    s.getRatingAvg(),
                    s.getDescription(),
                    s.getOpeningHours(),
                    s.getOwnerId()));
  }

  public java.util.List<StoreDto> list(String category, String status) {
    java.util.List<com.example.store.entity.Store> all = new java.util.ArrayList<>();
    storeRepository.findAll().forEach(all::add);
    java.util.stream.Stream<com.example.store.entity.Store> stream = all.stream();
    if (category != null && !category.isEmpty())
      stream = stream.filter(s -> category.equals(s.getCategory()));
    if (status != null && !status.isEmpty())
      stream = stream.filter(s -> status.equals(s.getStatus()));
    return stream
        .map(
            s ->
                new StoreDto(
                    s.getId(),
                    s.getName(),
                    s.getAddress(),
                    s.getPhone(),
                    s.getCategory(),
                    s.getStatus(),
                    s.getLatitude(),
                    s.getLongitude(),
                    s.getMinOrderAmount(),
                    s.getRatingAvg(),
                    s.getDescription(),
                    s.getOpeningHours(),
                    s.getOwnerId()))
        .collect(java.util.stream.Collectors.toList());
  }
}

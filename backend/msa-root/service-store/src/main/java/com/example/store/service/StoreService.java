package com.example.store.service;

import com.example.store.dto.StoreDto;
import com.example.store.entity.Store;
import com.example.store.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional
    public StoreDto createStore(String name, String address, String phone) {
        Store s = Store.builder()
                .id(null)
                .name(name)
                .address(address)
                .phone(phone)
                .createdAt(Instant.now())
                .build();
        Store saved = storeRepository.save(s);
        return new StoreDto(saved.getId(), saved.getName(), saved.getAddress(), saved.getPhone());
    }

    public Optional<StoreDto> findById(Long id) {
        return storeRepository.findById(id).map(s -> new StoreDto(s.getId(), s.getName(), s.getAddress(), s.getPhone()));
    }
}

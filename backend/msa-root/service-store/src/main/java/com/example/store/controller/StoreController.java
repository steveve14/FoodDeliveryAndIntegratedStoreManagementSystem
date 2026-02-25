package com.example.store.controller;

import com.example.store.dto.CreateStoreRequest;
import com.example.store.dto.StoreDto;
import com.example.store.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    public ResponseEntity<StoreDto> create(@RequestBody CreateStoreRequest req) {
        StoreDto dto = storeService.createStore(req.getName(), req.getAddress(), req.getPhone());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> get(@PathVariable Long id) {
        return storeService.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

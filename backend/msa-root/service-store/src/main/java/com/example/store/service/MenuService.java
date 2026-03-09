package com.example.store.service;

import com.example.store.dto.MenuDto;
import com.example.store.entity.Menu;
import com.example.store.entity.Store;
import com.example.store.repository.MenuRepository;
import com.example.store.repository.StoreRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/** Service handling menu items for stores (list, create, update, delete). */
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    public MenuService(MenuRepository menuRepository, StoreRepository storeRepository) {
        this.menuRepository = menuRepository;
        this.storeRepository = storeRepository;
    }

    public List<MenuDto> listByStore(String storeId) {
        return menuRepository.findByStoreId(storeId).stream()
                .map(
                        m -> new MenuDto(
                                m.getId(),
                                m.getStoreId(),
                                m.getName(),
                                m.getDescription(),
                                m.getPrice(),
                                m.isAvailable(),
                                m.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuDto create(String storeId, String userId, String userRole, MenuDto req) {
        assertStoreOwnership(storeId, userId, userRole);

        Menu m = Menu.builder()
                .id(UUID.randomUUID().toString())
                .storeId(storeId)
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .available(req.isAvailable())
                .createdAt(Instant.now())
                .isNewEntity(true)
                .build();
        Menu saved = menuRepository.save(m);
        return new MenuDto(
                saved.getId(),
                saved.getStoreId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.isAvailable(),
                saved.getCreatedAt());
    }

    @Transactional
    public MenuDto update(String storeId, String menuId, String userId, String userRole, MenuDto req) {
        Menu m = menuRepository
                .findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        if (!m.getStoreId().equals(storeId))
            throw new IllegalArgumentException("Forbidden");
        assertStoreOwnership(storeId, userId, userRole);
        Menu updated = m.toBuilder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .available(req.isAvailable())
                .build();
        Menu saved = menuRepository.save(updated);
        return new MenuDto(
                saved.getId(),
                saved.getStoreId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.isAvailable(),
                saved.getCreatedAt());
    }

    @Transactional
    public void delete(String storeId, String menuId, String userId, String userRole) {
        Menu m = menuRepository
                .findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found"));
        if (!m.getStoreId().equals(storeId))
            throw new IllegalArgumentException("Forbidden");
        assertStoreOwnership(storeId, userId, userRole);
        menuRepository.deleteById(menuId);
    }

    private void assertStoreOwnership(String storeId, String userId, String userRole) {
        if ("ADMIN".equals(userRole)) {
            return;
        }

        Store store = storeRepository
                .findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
        if (!store.getOwnerId().equals(userId)) {
            throw new IllegalArgumentException("Forbidden");
        }
    }
}

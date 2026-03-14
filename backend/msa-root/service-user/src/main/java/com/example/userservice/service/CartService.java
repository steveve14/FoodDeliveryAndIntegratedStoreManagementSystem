package com.example.userservice.service;

import com.example.userservice.dto.CartItemDto;
import com.example.userservice.dto.CartItemRequest;
import com.example.userservice.entity.CartItem;
import com.example.userservice.repository.CartItemRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** CartService 타입입니다. */
@Service
public class CartService {

  private final CartItemRepository cartItemRepository;

  public CartService(CartItemRepository cartItemRepository) {
    this.cartItemRepository = cartItemRepository;
  }

  public List<CartItemDto> listByUser(String userId) {
    return cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(this::toDto)
        .toList();
  }

  @Transactional
  public CartItemDto addItem(String userId, CartItemRequest request) {
    CartItem cartItem =
        cartItemRepository
            .findByUserIdAndStoreIdAndMenuId(userId, request.getStoreId(), request.getMenuId())
            .map(
                existing ->
                    existing.toBuilder()
                        .quantity(existing.getQuantity() + request.getQuantity())
                        .build())
            .orElseGet(
                () ->
                    CartItem.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .storeId(request.getStoreId())
                        .storeName(request.getStoreName())
                        .menuId(request.getMenuId())
                        .menuName(request.getMenuName())
                        .quantity(request.getQuantity())
                        .price(request.getPrice())
                        .createdAt(Instant.now())
                        .isNewEntity(true)
                        .build());

    CartItem saved = cartItemRepository.save(cartItem);
    return toDto(saved);
  }

  @Transactional
  public void deleteItem(String userId, String cartItemId) {
    CartItem existing =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니 항목을 찾을 수 없습니다."));
    ensureOwner(userId, existing.getUserId());
    cartItemRepository.deleteById(cartItemId);
  }

  @Transactional
  public void clear(String userId) {
    cartItemRepository
        .findByUserIdOrderByCreatedAtDesc(userId)
        .forEach(item -> cartItemRepository.deleteById(item.getId()));
  }

  private void ensureOwner(String userId, String ownerId) {
    if (!userId.equals(ownerId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }
  }

  private CartItemDto toDto(CartItem cartItem) {
    return new CartItemDto(
        cartItem.getId(),
        cartItem.getUserId(),
        cartItem.getStoreId(),
        cartItem.getStoreName(),
        cartItem.getMenuId(),
        cartItem.getMenuName(),
        cartItem.getQuantity(),
        cartItem.getPrice(),
        cartItem.getCreatedAt());
  }
}

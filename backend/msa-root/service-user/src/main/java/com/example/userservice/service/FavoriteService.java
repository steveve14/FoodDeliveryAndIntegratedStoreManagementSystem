package com.example.userservice.service;

import com.example.userservice.dto.FavoriteStoreDto;
import com.example.userservice.dto.FavoriteStoreRequest;
import com.example.userservice.entity.FavoriteStore;
import com.example.userservice.repository.FavoriteStoreRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** FavoriteService 타입입니다. */
@Service
public class FavoriteService {

  private final FavoriteStoreRepository favoriteStoreRepository;

  public FavoriteService(FavoriteStoreRepository favoriteStoreRepository) {
    this.favoriteStoreRepository = favoriteStoreRepository;
  }

  public List<FavoriteStoreDto> listByUser(String userId) {
    return favoriteStoreRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(this::toDto)
        .toList();
  }

  @Transactional
  public FavoriteStoreDto add(String userId, FavoriteStoreRequest request) {
    FavoriteStore favoriteStore =
        favoriteStoreRepository
            .findByUserIdAndStoreId(userId, request.getStoreId())
            .map(
                existing ->
                    existing.toBuilder()
                        .name(request.getName())
                        .category(request.getCategory())
                        .rating(request.getRating())
                        .deliveryTime(request.getDeliveryTime())
                        .minOrder(request.getMinOrder())
                        .imageIcon(request.getImageIcon())
                        .build())
            .orElseGet(
                () ->
                    FavoriteStore.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(userId)
                        .storeId(request.getStoreId())
                        .name(request.getName())
                        .category(request.getCategory())
                        .rating(request.getRating())
                        .deliveryTime(request.getDeliveryTime())
                        .minOrder(request.getMinOrder())
                        .imageIcon(request.getImageIcon())
                        .createdAt(Instant.now())
                        .isNewEntity(true)
                        .build());

    FavoriteStore saved = favoriteStoreRepository.save(favoriteStore);
    return toDto(saved);
  }

  @Transactional
  public void delete(String userId, String favoriteId) {
    FavoriteStore favoriteStore =
        favoriteStoreRepository
            .findById(favoriteId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "찜한 가게를 찾을 수 없습니다."));
    ensureOwner(userId, favoriteStore.getUserId());
    favoriteStoreRepository.deleteById(favoriteId);
  }

  private void ensureOwner(String userId, String ownerId) {
    if (!userId.equals(ownerId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }
  }

  private FavoriteStoreDto toDto(FavoriteStore favoriteStore) {
    return new FavoriteStoreDto(
        favoriteStore.getId(),
        favoriteStore.getUserId(),
        favoriteStore.getStoreId(),
        favoriteStore.getName(),
        favoriteStore.getCategory(),
        favoriteStore.getRating(),
        favoriteStore.getDeliveryTime(),
        favoriteStore.getMinOrder(),
        favoriteStore.getImageIcon(),
        favoriteStore.getCreatedAt());
  }
}

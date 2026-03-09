package com.example.store.service;

import com.example.store.dto.StoreDto;
import com.example.store.entity.Menu;
import com.example.store.entity.Store;
import com.example.store.repository.MenuRepository;
import com.example.store.repository.StoreRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
/** Service handling store management operations (create, find, list). */
public class StoreService {
  private final StoreRepository storeRepository;
  private final MenuRepository menuRepository;

  public StoreService(StoreRepository storeRepository, MenuRepository menuRepository) {
    this.storeRepository = storeRepository;
    this.menuRepository = menuRepository;
  }

  @Transactional
  public StoreDto createStore(com.example.store.dto.CreateStoreRequest req) {
    return createStore(req, req.getOwnerId(), "ADMIN");
  }

  @Transactional
  public StoreDto createStore(
      com.example.store.dto.CreateStoreRequest req, String currentUserId, String currentUserRole) {
    String ownerId = currentUserId;
    if ("ADMIN".equals(currentUserRole) && req.getOwnerId() != null && !req.getOwnerId().isBlank()) {
      ownerId = req.getOwnerId();
    }

    Store s = Store.builder()
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
        .ownerId(ownerId)
        .createdAt(Instant.now())
        .isNewEntity(true)
        .build();
    Store saved = storeRepository.save(s);
    return toDto(saved);
  }

  public Optional<StoreDto> findById(String id) {
    return storeRepository.findById(id).map(this::toDto);
  }

  public Optional<StoreDto> findByOwnerId(String ownerId) {
    return storeRepository.findByOwnerId(ownerId).map(this::toDto);
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
        .map(this::toDto)
        .collect(java.util.stream.Collectors.toList());
  }

  private StoreDto toDto(Store store) {
    List<Menu> menus = menuRepository.findByStoreId(store.getId());
    String category = store.getCategory() != null && !store.getCategory().isBlank()
      ? store.getCategory()
      : "기타";
    String bestseller = menus.stream()
      .filter(Menu::isAvailable)
      .map(Menu::getName)
      .findFirst()
      .orElseGet(() -> menus.stream().map(Menu::getName).findFirst().orElse("대표 메뉴 준비 중"));

    return new StoreDto(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getPhone(),
        store.getCategory(),
        store.getStatus(),
        store.getLatitude(),
        store.getLongitude(),
        store.getMinOrderAmount(),
        store.getRatingAvg(),
        store.getDescription(),
        store.getOpeningHours(),
        store.getOwnerId(),
        resolveEta(category),
        0,
        resolveDeliveryFee(menus),
        resolveHeroIcon(category),
        resolveTags(category, menus),
        bestseller,
        resolvePromo(store, menus, bestseller));
  }

  private String resolveEta(String category) {
    return switch (category) {
      case "치킨" -> "26~33분";
      case "한식" -> "18~25분";
      case "분식" -> "20~30분";
      case "일식" -> "35~45분";
      case "디저트" -> "15~25분";
      default -> "30~40분";
    };
  }

  private String resolveDeliveryFee(List<Menu> menus) {
    return menus.size() >= 3 ? "무료배달" : "2,000원";
  }

  private String resolveHeroIcon(String category) {
    return switch (category) {
      case "치킨" -> "i-lucide-drumstick";
      case "한식" -> "i-lucide-soup";
      case "분식" -> "i-lucide-flame";
      case "일식" -> "i-lucide-fish";
      case "디저트" -> "i-lucide-ice-cream-cone";
      default -> "i-lucide-store";
    };
  }

  private List<String> resolveTags(String category, List<Menu> menus) {
    List<String> tags = switch (category) {
      case "치킨" -> List.of("인기", "바삭", "야식");
      case "한식" -> List.of("혼밥", "점심추천", "가성비");
      case "분식" -> List.of("매운맛", "세트할인", "야식");
      case "일식" -> List.of("프리미엄", "신선", "저녁추천");
      case "디저트" -> List.of("달콤함", "간식", "커피와 함께");
      default -> List.of("주문 가능");
    };

    return menus.isEmpty() ? List.of("메뉴 준비 중") : tags;
  }

  private String resolvePromo(Store store, List<Menu> menus, String bestseller) {
    if (menus.isEmpty()) {
      return "현재 준비된 프로모션이 없습니다.";
    }
    if (store.getStatus() != null && !"OPEN".equalsIgnoreCase(store.getStatus())) {
      return "영업 시작 전이므로 오픈 후 주문 가능 여부를 확인해 주세요.";
    }
    return bestseller + " 메뉴를 바로 장바구니에 담을 수 있습니다.";
  }
}

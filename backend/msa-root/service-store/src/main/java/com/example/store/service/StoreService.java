package com.example.store.service;

import com.example.store.dto.StoreDto;
import com.example.store.entity.Menu;
import com.example.store.entity.Store;
import com.example.store.repository.MenuRepository;
import com.example.store.repository.StoreRepository;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** StoreService 타입입니다. */
@Service
public class StoreService {
  private record CategoryProfile(String eta, String heroIcon, List<String> tags) {}

  private static final CategoryProfile DEFAULT_PROFILE =
      new CategoryProfile("30~40분", "i-lucide-store", List.of("주문 가능"));

  private static final java.util.Map<String, CategoryProfile> CATEGORY_PROFILES =
      java.util.Map.ofEntries(
          java.util.Map.entry(
              "KOREAN",
              new CategoryProfile("18~25분", "i-lucide-soup", List.of("혼밥", "점심추천", "가성비"))),
          java.util.Map.entry(
              "CHINESE",
              new CategoryProfile(
                  "25~35분", "i-lucide-utensils-crossed", List.of("면요리", "든든한", "불맛"))),
          java.util.Map.entry(
              "JAPANESE",
              new CategoryProfile("30~40분", "i-lucide-fish", List.of("신선", "정갈함", "저녁추천"))),
          java.util.Map.entry(
              "WESTERN",
              new CategoryProfile("25~35분", "i-lucide-cooking-pot", List.of("파스타", "브런치", "데이트"))),
          java.util.Map.entry(
              "CHICKEN",
              new CategoryProfile("26~33분", "i-lucide-drumstick", List.of("인기", "바삭", "야식"))),
          java.util.Map.entry(
              "PIZZA",
              new CategoryProfile("28~38분", "i-lucide-pizza", List.of("파티", "치즈", "세트할인"))),
          java.util.Map.entry(
              "BURGER",
              new CategoryProfile("20~30분", "i-lucide-sandwich", List.of("한끼", "감튀추가", "빠른조리"))),
          java.util.Map.entry(
              "CAFE", new CategoryProfile("15~25분", "i-lucide-coffee", List.of("브런치", "커피", "여유"))),
          java.util.Map.entry(
              "DESSERT",
              new CategoryProfile(
                  "15~25분", "i-lucide-ice-cream-cone", List.of("달콤함", "간식", "커피와 함께"))),
          java.util.Map.entry(
              "SNACK", new CategoryProfile("20~30분", "i-lucide-flame", List.of("매운맛", "분식", "야식"))),
          java.util.Map.entry(
              "NIGHT",
              new CategoryProfile("30~45분", "i-lucide-moon-star", List.of("늦은밤", "안주", "야식"))),
          java.util.Map.entry(
              "BOSSAM", new CategoryProfile("32~45분", "i-lucide-beef", List.of("족발", "보쌈", "푸짐함"))),
          java.util.Map.entry(
              "ASIAN",
              new CategoryProfile("25~35분", "i-lucide-chef-hat", List.of("쌀국수", "커리", "이국적"))),
          java.util.Map.entry(
              "SALAD",
              new CategoryProfile("18~25분", "i-lucide-leaf", List.of("가벼운", "건강식", "다이어트"))),
          java.util.Map.entry(
              "LUNCHBOX",
              new CategoryProfile("20~30분", "i-lucide-package", List.of("도시락", "간편식", "점심추천"))),
          java.util.Map.entry("OTHER", DEFAULT_PROFILE));

  private final StoreRepository storeRepository;
  private final MenuRepository menuRepository;

  /** 가게 서비스를 생성합니다. */
  public StoreService(StoreRepository storeRepository, MenuRepository menuRepository) {
    this.storeRepository = storeRepository;
    this.menuRepository = menuRepository;
  }

  /** 가게를 생성합니다. */
  @Transactional
  public StoreDto createStore(com.example.store.dto.CreateStoreRequest req) {
    return createStore(req, req.getOwnerId(), "ADMIN");
  }

  /** 사용자 권한을 고려해 가게를 생성합니다. */
  @Transactional
  public StoreDto createStore(
      com.example.store.dto.CreateStoreRequest req, String currentUserId, String currentUserRole) {
    String ownerId = currentUserId;
    if ("ADMIN".equals(currentUserRole)
        && req.getOwnerId() != null
        && !req.getOwnerId().isBlank()) {
      ownerId = req.getOwnerId();
    }

    Store s =
        Store.builder()
            .id(java.util.UUID.randomUUID().toString())
            .name(req.getName())
            .address(req.getAddress())
            .phone(req.getPhone())
            .category(normalizeCategoryCode(req.getCategory()))
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

  /** ID로 가게를 조회합니다. */
  public Optional<StoreDto> findById(String id) {
    return storeRepository.findById(id).map(this::toDto);
  }

  /** 점주 ID로 가게를 조회합니다. */
  public Optional<StoreDto> findByOwnerId(String ownerId) {
    return storeRepository.findByOwnerId(ownerId).map(this::toDto);
  }

  /** 조건에 맞는 가게 목록을 조회합니다. */
  public java.util.List<StoreDto> list(String category, String status) {
    String normalizedCategory = normalizeCategoryCode(category);
    java.util.List<com.example.store.entity.Store> all = new java.util.ArrayList<>();
    storeRepository.findAll().forEach(all::add);
    java.util.stream.Stream<com.example.store.entity.Store> stream = all.stream();
    if (category != null && !category.isBlank()) {
      stream =
          stream.filter(s -> normalizedCategory.equals(normalizeCategoryCode(s.getCategory())));
    }
    if (status != null && !status.isEmpty()) {
      stream = stream.filter(s -> status.equals(s.getStatus()));
    }
    return stream.map(this::toDto).collect(java.util.stream.Collectors.toList());
  }

  private StoreDto toDto(Store store) {
    List<Menu> menus = menuRepository.findByStoreId(store.getId());
    String categoryCode = normalizeCategoryCode(store.getCategory());
    String bestseller =
        menus.stream()
            .filter(Menu::isAvailable)
            .map(Menu::getName)
            .findFirst()
            .orElseGet(() -> menus.stream().map(Menu::getName).findFirst().orElse("대표 메뉴 준비 중"));

    return new StoreDto(
        store.getId(),
        store.getName(),
        store.getAddress(),
        store.getPhone(),
        categoryCode,
        store.getStatus(),
        store.getLatitude(),
        store.getLongitude(),
        store.getMinOrderAmount(),
        store.getRatingAvg(),
        store.getDescription(),
        store.getOpeningHours(),
        store.getOwnerId(),
        resolveEta(categoryCode),
        0,
        resolveDeliveryFee(menus),
        resolveHeroIcon(categoryCode),
        resolveTags(categoryCode, menus),
        bestseller,
        resolvePromo(store, menus, bestseller));
  }

  private String resolveEta(String category) {
    return CATEGORY_PROFILES.getOrDefault(normalizeCategoryCode(category), DEFAULT_PROFILE).eta();
  }

  private String resolveDeliveryFee(List<Menu> menus) {
    return menus.size() >= 3 ? "무료배달" : "2,000원";
  }

  private String resolveHeroIcon(String category) {
    return CATEGORY_PROFILES
        .getOrDefault(normalizeCategoryCode(category), DEFAULT_PROFILE)
        .heroIcon();
  }

  private List<String> resolveTags(String category, List<Menu> menus) {
    List<String> tags =
        CATEGORY_PROFILES.getOrDefault(normalizeCategoryCode(category), DEFAULT_PROFILE).tags();

    return menus.isEmpty() ? List.of("메뉴 준비 중") : tags;
  }

  private String normalizeCategoryCode(String category) {
    if (category == null || category.isBlank()) {
      return "OTHER";
    }

    String normalized = category.trim().toUpperCase(Locale.ROOT);

    return switch (normalized) {
      case "한식" -> "KOREAN";
      case "중식" -> "CHINESE";
      case "일식" -> "JAPANESE";
      case "양식" -> "WESTERN";
      case "치킨" -> "CHICKEN";
      case "피자" -> "PIZZA";
      case "버거" -> "BURGER";
      case "카페" -> "CAFE";
      case "디저트" -> "DESSERT";
      case "분식" -> "SNACK";
      case "야식" -> "NIGHT";
      case "족발/보쌈", "족발", "보쌈" -> "BOSSAM";
      case "아시안" -> "ASIAN";
      case "샐러드" -> "SALAD";
      case "도시락" -> "LUNCHBOX";
      case "기타" -> "OTHER";
      default -> CATEGORY_PROFILES.containsKey(normalized) ? normalized : "OTHER";
    };
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

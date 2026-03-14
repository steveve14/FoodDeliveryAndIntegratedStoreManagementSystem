package com.example.userservice.repository;

import com.example.userservice.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** CartItemRepository 타입입니다. */
@Repository
public interface CartItemRepository extends CrudRepository<CartItem, String> {

  /** 사용자별 장바구니 항목을 최신순으로 조회합니다. */
  List<CartItem> findByUserIdOrderByCreatedAtDesc(String userId);

  /** 사용자/가게/메뉴 기준으로 장바구니 항목을 조회합니다. */
  Optional<CartItem> findByUserIdAndStoreIdAndMenuId(String userId, String storeId, String menuId);
}

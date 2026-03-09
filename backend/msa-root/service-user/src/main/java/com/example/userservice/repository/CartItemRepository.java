package com.example.userservice.repository;

import com.example.userservice.entity.CartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends CrudRepository<CartItem, String> {
    List<CartItem> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<CartItem> findByUserIdAndStoreIdAndMenuId(String userId, String storeId, String menuId);
}
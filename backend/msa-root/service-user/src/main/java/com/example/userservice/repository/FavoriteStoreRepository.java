package com.example.userservice.repository;

import com.example.userservice.entity.FavoriteStore;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** FavoriteStoreRepository 타입입니다. */
@Repository
public interface FavoriteStoreRepository extends CrudRepository<FavoriteStore, String> {
  List<FavoriteStore> findByUserIdOrderByCreatedAtDesc(String userId);

  Optional<FavoriteStore> findByUserIdAndStoreId(String userId, String storeId);
}

package com.example.userservice.repository;

import com.example.userservice.entity.FavoriteStore;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** FavoriteStoreRepository 타입입니다. */
@Repository
public interface FavoriteStoreRepository extends CrudRepository<FavoriteStore, String> {

  /** 사용자별 찜한 가게 목록을 최신순으로 조회합니다. */
  List<FavoriteStore> findByUserIdOrderByCreatedAtDesc(String userId);

  /** 사용자와 가게 ID 기준으로 찜 정보를 조회합니다. */
  Optional<FavoriteStore> findByUserIdAndStoreId(String userId, String storeId);
}

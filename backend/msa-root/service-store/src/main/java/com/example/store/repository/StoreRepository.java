package com.example.store.repository;

import com.example.store.entity.Store;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** 가게 엔티티 저장소입니다. */
@Repository
public interface StoreRepository extends CrudRepository<Store, String> {

  /** 점주 ID로 가게를 조회합니다. */
  Optional<Store> findByOwnerId(String ownerId);
}

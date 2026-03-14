package com.example.store.repository;

import com.example.store.entity.Menu;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** 메뉴 엔티티 저장소입니다. */
@Repository
public interface MenuRepository extends CrudRepository<Menu, String> {

  /** 매장 ID로 메뉴 목록을 조회합니다. */
  List<Menu> findByStoreId(String storeId);
}

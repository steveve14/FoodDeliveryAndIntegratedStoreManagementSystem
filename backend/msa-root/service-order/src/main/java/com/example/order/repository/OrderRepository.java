package com.example.order.repository;

import com.example.order.entity.Order;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** 주문 엔티티 저장소입니다. */
@Repository
public interface OrderRepository extends CrudRepository<Order, String> {

  /** 사용자 ID 기준으로 최신 주문 목록을 조회합니다. */
  List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

  /** 매장 ID 기준으로 최신 주문 목록을 조회합니다. */
  List<Order> findByStoreIdOrderByCreatedAtDesc(String storeId);
}

package com.example.order.repository;

import com.example.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** 주문 항목 엔티티 저장소입니다. */
@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, String> {

  /** 주문 ID로 주문 항목 목록을 조회합니다. */
  List<OrderItem> findByOrderId(String orderId);
}

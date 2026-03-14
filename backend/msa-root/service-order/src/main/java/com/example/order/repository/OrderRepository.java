package com.example.order.repository;

import com.example.order.entity.Order;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/** Repository for Order entities. */
/** OrderRepository 타입입니다. */
public interface OrderRepository extends CrudRepository<Order, String> {
  List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

  List<Order> findByStoreIdOrderByCreatedAtDesc(String storeId);
}

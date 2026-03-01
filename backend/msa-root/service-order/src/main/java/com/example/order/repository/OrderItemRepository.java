package com.example.order.repository;

import com.example.order.entity.OrderItem;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/** Repository for OrderItem entities. */
public interface OrderItemRepository extends CrudRepository<OrderItem, String> {

  List<OrderItem> findByOrderId(String orderId);
}

package com.example.order.repository;

import com.example.order.entity.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/** Repository for Order entities. */
public interface OrderRepository extends CrudRepository<Order, String> {}

package com.example.delivery.repository;

import com.example.delivery.entity.Delivery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** DeliveryRepository 타입입니다. */
@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, String> {}

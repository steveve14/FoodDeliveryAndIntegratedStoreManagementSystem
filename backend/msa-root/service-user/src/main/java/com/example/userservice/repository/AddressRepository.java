package com.example.userservice.repository;

import com.example.userservice.entity.Address;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/*
 * Repository for Address entities.
 */
/** AddressRepository 타입입니다. */
public interface AddressRepository extends CrudRepository<Address, String> {

  List<Address> findByUserId(String userId);
}

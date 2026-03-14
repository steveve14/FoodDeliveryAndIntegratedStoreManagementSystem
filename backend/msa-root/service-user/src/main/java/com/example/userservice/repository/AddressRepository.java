package com.example.userservice.repository;

import com.example.userservice.entity.Address;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** 주소 엔티티 저장소입니다. */
@Repository
public interface AddressRepository extends CrudRepository<Address, String> {

  /** 사용자 ID 기준 주소 목록을 조회합니다. */
  List<Address> findByUserId(String userId);
}

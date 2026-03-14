package com.example.userservice.repository;

import com.example.userservice.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** 사용자 엔티티 저장소입니다. */
@Repository
public interface UserRepository extends CrudRepository<User, String> {

  /** 이메일로 사용자를 조회합니다. */
  Optional<User> findByEmail(String email);

  /** 역할 기준으로 사용자 목록을 조회합니다. */
  List<User> findByRolesOrderByCreatedAtDesc(String roles);

  /** 팀 역할이 있는 사용자 목록을 역할 기준으로 조회합니다. */
  List<User> findByRolesAndTeamRoleIsNotNullOrderByCreatedAtDesc(String roles);
}

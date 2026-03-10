package com.example.userservice.repository;

import com.example.userservice.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
/*
 * Repository for User entities.
 */
public interface UserRepository extends CrudRepository<User, String> {

  Optional<User> findByEmail(String email);

  List<User> findByRolesOrderByCreatedAtDesc(String roles);

  List<User> findByRolesAndTeamRoleIsNotNullOrderByCreatedAtDesc(String roles);
}

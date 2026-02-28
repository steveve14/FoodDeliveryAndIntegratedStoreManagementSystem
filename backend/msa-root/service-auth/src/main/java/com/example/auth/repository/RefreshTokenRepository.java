package com.example.auth.repository;

import com.example.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

  java.util.Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);
}

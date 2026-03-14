package com.example.auth.repository;

import com.example.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** RefreshTokenRepository 타입입니다. */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

  java.util.Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);
}

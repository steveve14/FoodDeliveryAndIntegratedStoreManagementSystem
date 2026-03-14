package com.example.auth.repository;

import com.example.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** RefreshTokenRepository 타입입니다. */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

  /** 토큰 문자열로 리프레시 토큰을 조회합니다. */
  java.util.Optional<RefreshToken> findByToken(String token);

  /** 토큰 문자열로 리프레시 토큰을 삭제합니다. */
  void deleteByToken(String token);
}

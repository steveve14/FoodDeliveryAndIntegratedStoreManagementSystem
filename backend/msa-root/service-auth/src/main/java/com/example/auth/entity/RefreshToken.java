package com.example.auth.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RefreshToken implements Persistable<String> {
  @Id
  @Column("id")
  private String id; // 🚨 Long에서 String으로 변경됨

  @Column("user_id")
  private String userId; // Long에서 String으로 변경됨

  @Column("token")
  private String token;

  @Column("revoked")
  private boolean revoked;

  @Column("created_at")
  private Instant createdAt;

  @Column("expires_at")
  private Instant expiresAt;

  @Transient
  private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

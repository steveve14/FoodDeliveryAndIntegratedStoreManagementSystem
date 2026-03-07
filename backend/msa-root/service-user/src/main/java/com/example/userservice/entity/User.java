package com.example.userservice.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
/** Represents a user account stored in the users table. */
public class User implements Persistable<String> {
  @Id
  @Column("id")
  private String id;

  @Column("email")
  private String email;

  @Column("password_hash")
  private String passwordHash;

  @Column("name")
  private String name;

  @Column("phone")
  private String phone;

  @Column("roles")
  private String roles;

  @Column("provider")
  private String provider; // e.g., "google", "kakao", or "local"

  @Column("provider_id")
  private String providerId; // external provider user id

  @Column("created_at")
  private Instant createdAt;

  @Transient @Builder.Default private boolean isNewEntity = true;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

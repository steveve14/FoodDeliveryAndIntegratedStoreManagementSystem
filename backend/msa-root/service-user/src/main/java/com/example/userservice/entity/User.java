package com.example.userservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
/**
 * Represents a user account stored in the users table.
 */
public class User implements Persistable<String> {
    @Id
    private String id;
    private String email;
    private String passwordHash;
    private String name;
    private String phone;
    private String roles;
    private String provider; // e.g., "google", "kakao", or "local"
    private String providerId; // external provider user id
    private Instant createdAt;

    @Transient
    @Builder.Default
    private boolean isNewEntity = true;

    @Override
    public boolean isNew() {
        return isNewEntity;
    }
}
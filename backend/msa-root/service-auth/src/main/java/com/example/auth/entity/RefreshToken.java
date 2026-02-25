package com.example.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("refresh_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RefreshToken {
    @Id
    private String id;            // 🚨 Long에서 String으로 변경됨
    private String userId;        // Long에서 String으로 변경됨
    private String token;
    private boolean revoked;
    private Instant createdAt;
    private Instant expiresAt;
}
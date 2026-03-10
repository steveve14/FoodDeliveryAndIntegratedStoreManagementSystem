package com.example.userservice.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserNotification implements Persistable<Long> {
    @Id
    @Column("id")
    private Long id;

    @Column("user_id")
    private String userId;

    @Column("body")
    private String body;

    @Column("unread")
    private boolean unread;

    @Column("created_at")
    private Instant createdAt;

    @Transient
    private boolean isNewEntity;

    @Override
    public boolean isNew() {
        return isNewEntity;
    }
}
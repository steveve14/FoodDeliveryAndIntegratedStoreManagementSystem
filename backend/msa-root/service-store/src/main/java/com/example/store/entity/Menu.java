package com.example.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("menu")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Menu implements Persistable<String> {
    @Id
    private String id;
    private String storeId;
    private String name;
    private String description;
    private long price;
    private boolean available;
    private Instant createdAt;

    @Transient
    @Builder.Default
    private boolean isNewEntity = true;

    @Override
    public boolean isNew() {
        return isNewEntity;
    }
}
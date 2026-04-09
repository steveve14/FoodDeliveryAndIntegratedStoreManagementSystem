package com.example.store.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/** Menu 타입입니다. */
@Table("menu")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Menu implements Persistable<String> {
  @Id private String id;
  private String storeId;
  private String name;
  private String description;
  private long price;
  private boolean available;
  private String imageUrl;
  private Instant createdAt;

  @Transient private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

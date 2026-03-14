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

/** FavoriteStore 타입입니다. */
@Table("favorite_stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FavoriteStore implements Persistable<String> {
  @Id
  @Column("id")
  private String id;

  @Column("user_id")
  private String userId;

  @Column("store_id")
  private String storeId;

  @Column("name")
  private String name;

  @Column("category")
  private String category;

  @Column("rating")
  private double rating;

  @Column("delivery_time")
  private String deliveryTime;

  @Column("min_order")
  private String minOrder;

  @Column("image_icon")
  private String imageIcon;

  @Column("created_at")
  private Instant createdAt;

  @Transient private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

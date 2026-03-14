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

/** CartItem 타입입니다. */
@Table("cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CartItem implements Persistable<String> {
  @Id
  @Column("id")
  private String id;

  @Column("user_id")
  private String userId;

  @Column("store_id")
  private String storeId;

  @Column("store_name")
  private String storeName;

  @Column("menu_id")
  private String menuId;

  @Column("menu_name")
  private String menuName;

  @Column("quantity")
  private int quantity;

  @Column("price")
  private int price;

  @Column("created_at")
  private Instant createdAt;

  @Transient private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

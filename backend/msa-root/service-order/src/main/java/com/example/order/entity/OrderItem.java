package com.example.order.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/** OrderItem 타입입니다. */
@Table("order_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderItem implements Persistable<String> {
  @Id private String id;
  private String orderId;
  private String menuId;
  private int quantity;
  private long priceSnapshot;

  @Transient private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

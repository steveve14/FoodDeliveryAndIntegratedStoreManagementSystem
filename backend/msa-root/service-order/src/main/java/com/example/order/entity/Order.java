package com.example.order.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order implements Persistable<String> {
  @Id
  private String id;
  private String userId;
  private String storeId;
  private int totalAmount;
  private String status;
  private Instant createdAt;

  @Transient
  private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

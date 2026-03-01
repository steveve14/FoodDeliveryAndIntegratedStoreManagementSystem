package com.example.delivery.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("delivery")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Delivery implements Persistable<String> {
  @Id private String id;
  private String orderId;
  private String courier;
  private String status;
  private int deliveryFee;
  private Instant scheduledAt;

  @Transient @Builder.Default private boolean isNewEntity = true;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

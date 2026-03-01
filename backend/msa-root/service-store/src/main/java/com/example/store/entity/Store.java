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

@Table("store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Store implements Persistable<String> {
  @Id private String id;
  private String name;
  private String address;
  private String phone;
  private String category;
  private String status; // e.g., OPEN/CLOSED
  private Double latitude;
  private Double longitude;
  private Long minOrderAmount;
  private Double ratingAvg;
  private String description;
  private String openingHours;
  private String ownerId;
  private Instant createdAt;

  @Transient @Builder.Default private boolean isNewEntity = true;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

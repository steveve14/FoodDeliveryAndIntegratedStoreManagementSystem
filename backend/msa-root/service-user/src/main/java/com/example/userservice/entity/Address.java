package com.example.userservice.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
/** Represents a user address stored in the addresses table. */
public class Address implements Persistable<String> {
  @Id private String id;
  private String userId;
  private String label; // e.g., "home", "work"
  private String line1;
  private String line2;
  private String city;
  private String state;
  private String postalCode;
  private String country;
  private boolean primaryAddress;
  private Instant createdAt;

  @Transient @Builder.Default private boolean isNewEntity = true;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

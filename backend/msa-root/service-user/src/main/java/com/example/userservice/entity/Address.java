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

@Table("addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
/** Represents a user address stored in the addresses table. */
public class Address implements Persistable<String> {
  @Id
  @Column("id")
  private String id;

  @Column("user_id")
  private String userId;

  @Column("label")
  private String label; // e.g., "home", "work"

  @Column("line1")
  private String line1;

  @Column("line2")
  private String line2;

  @Column("city")
  private String city;

  @Column("state")
  private String state;

  @Column("postal_code")
  private String postalCode;

  @Column("country")
  private String country;

  @Column("primary_address")
  private boolean primaryAddress;

  @Column("created_at")
  private Instant createdAt;

  @Transient
  private boolean isNewEntity;

  @Override
  public boolean isNew() {
    return isNewEntity;
  }
}

package com.example.userservice.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private String id;
    private String userId;
    private String storeId;
    private String storeName;
    private String menuId;
    private String menuName;
    private int quantity;
    private int price;
    private Instant createdAt;
}
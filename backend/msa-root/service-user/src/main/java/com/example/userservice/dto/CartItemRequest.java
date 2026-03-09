package com.example.userservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @NotBlank
    private String storeId;

    @NotBlank
    private String storeName;

    @NotBlank
    private String menuId;

    @NotBlank
    private String menuName;

    @Min(1)
    private int quantity;

    @Min(0)
    private int price;
}
package com.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteStoreRequest {
    @NotBlank
    private String storeId;

    @NotBlank
    private String name;

    @NotBlank
    private String category;

    private double rating;

    @NotBlank
    private String deliveryTime;

    @NotBlank
    private String minOrder;

    @NotBlank
    private String imageIcon;
}
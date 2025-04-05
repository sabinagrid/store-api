package com.storeapi.dto;

import lombok.Data;

@Data
public class CartItemDto {
    Long productId;
    int quantity;
}

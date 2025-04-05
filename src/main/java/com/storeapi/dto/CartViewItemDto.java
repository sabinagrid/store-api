package com.storeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CartViewItemDto {
    int ordinal;
    String productName;
    int quantity;
    BigDecimal price;
}

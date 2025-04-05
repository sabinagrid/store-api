package com.storeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartViewResponse {
    List<CartViewItemDto> items;
    BigDecimal subtotal;
}

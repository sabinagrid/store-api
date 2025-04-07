package com.storeapi.dto;

import com.storeapi.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSummaryDto {
    private Long id;
    private LocalDateTime createdAt;
    private BigDecimal total;
    private OrderStatus status;
}

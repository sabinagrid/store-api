package com.storeapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderSummaryDto {
    Long orderId;
    LocalDateTime date;
    BigDecimal total;
    String status;
}

package com.storeapi.dto;

import lombok.Data;

@Data
public class ModifyCartRequest {
    Long productId;
    int quantity;
}

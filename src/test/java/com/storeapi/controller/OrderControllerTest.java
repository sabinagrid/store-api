package com.storeapi.controller;

import com.storeapi.dto.OrderSummaryDto;
import com.storeapi.entity.User;
import com.storeapi.entity.OrderStatus;
import com.storeapi.service.OrderService;
import com.storeapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @Test
    void checkout_shouldReturn200_whenAuthorized() throws Exception {
        Mockito.when(userService.getUserBySession("session1")).thenReturn(Optional.of(new User()));
        Mockito.when(orderService.checkout(any(), any())).thenReturn(null);

        mockMvc.perform(post("/api/orders/checkout")
                        .header("Authorization", "session1"))
                .andExpect(status().isOk());
    }

    @Test
    void cancel_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/orders/1/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrders_shouldReturn200_whenAuthorized() throws Exception {
        Mockito.when(userService.getUserBySession("session1")).thenReturn(Optional.of(new User()));

        OrderSummaryDto summary = new OrderSummaryDto(
                1L,
                LocalDateTime.now(),
                BigDecimal.valueOf(99.99),
                OrderStatus.PLACED
        );

        Mockito.when(orderService.getUserOrders(any())).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/orders")
                        .header("Authorization", "session1"))
                .andExpect(status().isOk());
    }
}

package com.storeapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.CartViewResponse;
import com.storeapi.dto.ModifyCartRequest;
import com.storeapi.service.CartService;
import com.storeapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addToCart_shouldReturn200() throws Exception {
        CartItemDto item = new CartItemDto(1L, 2);
        mockMvc.perform(post("/api/cart")
                        .header("Authorization", "session1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk());
    }

    @Test
    void removeItem_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/cart/1")
                        .header("Authorization", "session1"))
                .andExpect(status().isOk());
    }

    @Test
    void viewCart_shouldReturn200() throws Exception {
        Mockito.when(cartService.viewCart("session1")).thenReturn(new CartViewResponse());
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "session1"))
                .andExpect(status().isOk());
    }

    @Test
    void modifyItem_shouldReturn200() throws Exception {
        ModifyCartRequest request = new ModifyCartRequest(1L, 3);
        mockMvc.perform(put("/api/cart")
                        .header("Authorization", "session1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void checkout_shouldReturn200() throws Exception {
        Mockito.when(cartService.checkout("session1")).thenReturn("Order placed successfully");
        mockMvc.perform(post("/api/cart/checkout")
                        .header("Authorization", "session1"))
                .andExpect(status().isOk());
    }
}

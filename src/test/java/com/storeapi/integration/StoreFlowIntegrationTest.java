package com.storeapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.LoginRequest;
import com.storeapi.dto.RegisterRequest;
import com.storeapi.entity.Product;
import com.storeapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class StoreFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        if (productRepository.findAll().isEmpty()) {
            Product p = new Product();
            p.setTitle("Hammer");
            p.setAvailable(10);
            p.setPrice(BigDecimal.valueOf(9.99));
            productRepository.save(p);
        }
    }

    @Test
    void fullFlow_shouldSucceed() throws Exception {
        // register
        RegisterRequest register = new RegisterRequest();
        register.setEmail("integration@test.com");
        register.setPassword("pass123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        // login
        LoginRequest login = new LoginRequest();
        login.setEmail("integration@test.com");
        login.setPassword("pass123");

        String sessionId = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replace("{\"sessionId\":\"", "")
                .replace("\"}", "");

        // add to cart
        Long productId = productRepository.findAll().get(0).getId();
        CartItemDto item = new CartItemDto();
        item.setProductId(productId);
        item.setQuantity(2);

        mockMvc.perform(post("/api/cart")
                        .header("Authorization", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk());

        // checkout
        mockMvc.perform(post("/api/cart/checkout")
                        .header("Authorization", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Order placed successfully")));
    }
}

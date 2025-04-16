package com.storeapi.service;

import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.ModifyCartRequest;
import com.storeapi.entity.Product;
import com.storeapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setAvailable(10);
        product.setPrice(BigDecimal.TEN);
        return product;
    }

    private CartItemDto createCartItem(int quantity) {
        CartItemDto dto = new CartItemDto();
        dto.setProductId(1L);
        dto.setQuantity(quantity);
        return dto;
    }

    @Test
    void addToCart_shouldAddItem_whenStockIsSufficient() {
        Product product = createProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addToCart("session1", createCartItem(2));

        List<CartItemDto> cart = cartService.getCart("session1");

        assertAll(
                () -> assertEquals(1, cart.size(), "Cart should contain 1 item"),
                () -> assertEquals(2, cart.get(0).getQuantity(), "Quantity should be 2")
        );
    }

    @Test
    void addToCart_shouldThrow_whenStockIsInsufficient() {
        Product product = createProduct();
        product.setAvailable(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () ->
                cartService.addToCart("session1", createCartItem(5))
        );
    }

    @Test
    void modifyItem_shouldUpdateQuantity() {
        Product product = createProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addToCart("session1", createCartItem(2));

        ModifyCartRequest req = new ModifyCartRequest();
        req.setProductId(1L);
        req.setQuantity(5);

        cartService.modifyItem("session1", req);

        assertEquals(5, cartService.getCart("session1").get(0).getQuantity());
    }

    @Test
    void removeItem_shouldRemoveFromCart() {
        Product product = createProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addToCart("session1", createCartItem(2));
        cartService.removeItem("session1", 1L);

        assertTrue(cartService.getCart("session1").isEmpty());
    }
}

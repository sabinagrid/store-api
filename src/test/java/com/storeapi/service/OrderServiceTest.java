package com.storeapi.service;

import com.storeapi.dto.CartItemDto;
import com.storeapi.entity.*;
import com.storeapi.repository.OrderRepository;
import com.storeapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkout_shouldPlaceOrderAndClearCart() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Item");
        product.setAvailable(10);
        product.setPrice(BigDecimal.TEN);

        CartItemDto cartItem = new CartItemDto();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);

        User user = new User();
        user.setId(1L);
        user.setEmail("email");
        user.setPasswordHash("hash");

        when(cartService.getCart("session1")).thenReturn(List.of(cartItem));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = orderService.checkout("session1", user);

        assertAll(
                () -> assertEquals(OrderStatus.PLACED, order.getStatus(), "Order status should be PLACED"),
                () -> assertEquals(1, order.getItems().size(), "Order should contain 1 item")
        );
    }

    @Test
    void cancelOrder_shouldUpdateStatusAndRestoreStock() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Test");
        product.setAvailable(0);
        product.setPrice(BigDecimal.TEN);

        OrderItem item = new OrderItem(1L, product, 3, BigDecimal.TEN);

        Order order = new Order();
        order.setId(1L);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setTotal(BigDecimal.TEN);
        order.setItems(List.of(item));
        order.setUser(new User());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertAll(
                () -> assertEquals(OrderStatus.CANCELLED, order.getStatus(), "Order status should be CANCELLED"),
                () -> assertEquals(3, product.getAvailable(), "Product stock should be restored to 3")
        );
    }
}

package com.storeapi.service;

import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.OrderSummaryDto;
import com.storeapi.entity.*;
import com.storeapi.repository.OrderRepository;
import com.storeapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public Order checkout(String sessionId, User user) {
        List<CartItemDto> cartItems = cartService.getCart(sessionId);
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDto dto : cartItems) {
            Product p = productRepository.findById(dto.getProductId()).orElseThrow();
            if (p.getAvailable() < dto.getQuantity()) throw new RuntimeException("Stock error");
            p.setAvailable(p.getAvailable() - dto.getQuantity());
            OrderItem orderItem = new OrderItem(null, p, dto.getQuantity(), p.getPrice());
            orderItems.add(orderItem);
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }
        Order order = new Order(null, LocalDateTime.now(), OrderStatus.PLACED, total, user, orderItems);
        cartService.clearCart(sessionId);
        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Order o = orderRepository.findById(orderId).orElseThrow();
        o.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : o.getItems()) {
            Product p = item.getProduct();
            p.setAvailable(p.getAvailable() + item.getQuantity());
        }
        orderRepository.save(o);
    }

    public List<OrderSummaryDto> getUserOrders(User user) {
        return orderRepository.findByUser(user).stream()
                .map(o -> new OrderSummaryDto(o.getId(), o.getCreatedAt(), o.getTotal(), o.getStatus()))
                .toList();
    }
}
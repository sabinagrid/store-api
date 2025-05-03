package com.storeapi.service;

import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.OrderSummaryDto;
import com.storeapi.entity.*;
import com.storeapi.repository.OrderRepository;
import com.storeapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
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
            Product p = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found with id: " + dto.getProductId()));

            if (p.getAvailable() < dto.getQuantity()) {
                throw new ResponseStatusException(BAD_REQUEST, "Not enough stock for product: " + p.getTitle());
            }

            p.setAvailable(p.getAvailable() - dto.getQuantity());
            OrderItem orderItem = new OrderItem(null, p, dto.getQuantity(), p.getPrice());
            orderItems.add(orderItem);
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }

        Order order = new Order(null, LocalDateTime.now(), OrderStatus.PLACED, total, user, orderItems);
        cartService.clearCart(sessionId);
        log.info("Order placed successfully: user={}, total={}, numberOfItems={}", user.getEmail(), total, orderItems.size());
        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Order o = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found with id: " + orderId));
        o.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : o.getItems()) {
            Product p = item.getProduct();
            p.setAvailable(p.getAvailable() + item.getQuantity());
        }
        orderRepository.save(o);
        log.info("Order cancelled: orderId={}", orderId);
    }

    public List<OrderSummaryDto> getUserOrders(User user) {
        return orderRepository.findByUser(user).stream()
                .map(o -> new OrderSummaryDto(o.getId(), o.getCreatedAt(), o.getTotal(), o.getStatus()))
                .toList();
    }
}
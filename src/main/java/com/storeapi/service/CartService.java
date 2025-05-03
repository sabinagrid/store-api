package com.storeapi.service;

import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.CartViewItemDto;
import com.storeapi.dto.CartViewResponse;
import com.storeapi.dto.ModifyCartRequest;
import com.storeapi.entity.Product;
import com.storeapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductRepository productRepository;
    private final Map<String, List<CartItemDto>> sessionCarts = new HashMap<>();

    public void addToCart(String sessionId, CartItemDto dto) {
        Product p = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found with id: " + dto.getProductId()));
        if (p.getAvailable() < dto.getQuantity())
            throw new ResponseStatusException(BAD_REQUEST, "Insufficient stock");

        List<CartItemDto> cart = sessionCarts.computeIfAbsent(sessionId, id -> new ArrayList<>());

        for (CartItemDto item : cart) {
            if (item.getProductId().equals(dto.getProductId())) {
                item.setQuantity(item.getQuantity() + dto.getQuantity());
                return;
            }
        }
        cart.add(dto);
    }

    public CartViewResponse viewCart(String sessionId) {
        List<CartItemDto> cart = sessionCarts.getOrDefault(sessionId, List.of());
        List<CartViewItemDto> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int idx = 1;
        for (CartItemDto dto : cart) {
            Product p = productRepository.findById(dto.getProductId()).orElseThrow();
            BigDecimal itemTotal = p.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
            items.add(new CartViewItemDto(idx++, p.getTitle(), dto.getQuantity(), p.getPrice()));
            subtotal = subtotal.add(itemTotal);
        }
        return new CartViewResponse(items, subtotal);
    }

    public void removeItem(String sessionId, Long productId) {
        List<CartItemDto> cart = sessionCarts.getOrDefault(sessionId, List.of());
        cart.removeIf(i -> i.getProductId().equals(productId));
    }

    public void modifyItem(String sessionId, ModifyCartRequest req) {
        List<CartItemDto> cart = sessionCarts.getOrDefault(sessionId, List.of());
        for (CartItemDto item : cart) {
            if (item.getProductId().equals(req.getProductId())) {
                item.setQuantity(req.getQuantity());
                return;
            }
        }
        throw new ResponseStatusException(NOT_FOUND, "Product not found in cart");
    }

    public List<CartItemDto> getCart(String sessionId) {
        return sessionCarts.getOrDefault(sessionId, List.of());
    }

    public void clearCart(String sessionId) {
        sessionCarts.remove(sessionId);
    }

    public void validateCartForCheckout(String sessionId) {
        List<CartItemDto> cart = sessionCarts.get(sessionId);
        if (cart == null || cart.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Cart is empty");
        }

        for (CartItemDto item : cart) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found: " + item.getProductId()));

            if (product.getAvailable() < item.getQuantity()) {
                throw new ResponseStatusException(BAD_REQUEST, "Not enough stock for product: " + product.getTitle());
            }
        }
    }
}
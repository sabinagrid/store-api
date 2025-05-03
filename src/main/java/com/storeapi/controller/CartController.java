package com.storeapi.controller;

import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.CartViewResponse;
import com.storeapi.dto.ModifyCartRequest;
import com.storeapi.entity.User;
import com.storeapi.service.CartService;
import com.storeapi.service.OrderService;
import com.storeapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/add")
    public void addToCart(@RequestHeader("Authorization") String sessionId, @RequestBody CartItemDto req) {
        cartService.addToCart(sessionId, req);
    }

    @GetMapping
    public CartViewResponse viewCart(@RequestHeader("Authorization") String sessionId) {
        return cartService.viewCart(sessionId);
    }

    @DeleteMapping("/{id}")
    public void removeItem(@RequestHeader("Authorization") String sessionId, @PathVariable Long id) {
        cartService.removeItem(sessionId, id);
    }

    @PutMapping("/modify")
    public void modifyItem(@RequestHeader("Authorization") String sessionId, @RequestBody ModifyCartRequest req) {
        cartService.modifyItem(sessionId, req);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestHeader("Authorization") String sessionId) {
        cartService.validateCartForCheckout(sessionId);
        User user = userService.getUserBySession(sessionId).orElseThrow();
        orderService.checkout(sessionId, user);
        return ResponseEntity.ok("Order placed successfully");
    }
}

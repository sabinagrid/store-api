package com.storeapi.controller;

import com.storeapi.dto.CartItemDto;
import com.storeapi.dto.CartViewResponse;
import com.storeapi.dto.ModifyCartRequest;
import com.storeapi.service.CartService;
import com.storeapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    @PostMapping
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

    @PutMapping
    public void modifyItem(@RequestHeader("Authorization") String sessionId, @RequestBody ModifyCartRequest req) {
        cartService.modifyItem(sessionId, req);
    }

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(@RequestHeader("Authorization") String sessionId) {
        String result = cartService.checkout(sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/view")
    public String viewCartPage(@RequestHeader("Authorization") String sessionId, Model model) {
        CartViewResponse response = cartService.viewCart(sessionId);
        model.addAttribute("cart", response);
        return "cart";
    }
}
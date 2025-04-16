package com.storeapi.controller;

import com.storeapi.dto.OrderSummaryDto;
import com.storeapi.entity.Order;
import com.storeapi.entity.User;
import com.storeapi.service.OrderService;
import com.storeapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/checkout")
    public Order checkout(@RequestHeader("Authorization") String sessionId) {
        User user = userService.getUserBySession(sessionId).orElseThrow();
        return orderService.checkout(sessionId, user);
    }

    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @GetMapping
    public List<OrderSummaryDto> getOrders(@RequestHeader("Authorization") String sessionId) {
        User user = userService.getUserBySession(sessionId).orElseThrow();
        return orderService.getUserOrders(user);
    }

    @GetMapping("/view")
    public String viewOrders(@RequestHeader("Authorization") String sessionId, Model model) {
        User user = userService.getUserBySession(sessionId).orElseThrow();
        model.addAttribute("orders", orderService.getUserOrders(user));
        return "orders";
    }
}
package com.storeapi.controller;

import com.storeapi.dto.ProductDto;
import com.storeapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> list() {
        return productService.getAllProducts();
    }
}

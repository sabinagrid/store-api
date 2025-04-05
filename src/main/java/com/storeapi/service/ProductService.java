package com.storeapi.service;

import com.storeapi.dto.ProductDto;
import com.storeapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> new ProductDto(p.getId(), p.getTitle(), p.getAvailable(), p.getPrice()))
                .toList();
    }
}

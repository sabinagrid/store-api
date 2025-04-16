package com.storeapi.service;

import com.storeapi.dto.ProductDto;
import com.storeapi.entity.Product;
import com.storeapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllProducts_shouldReturnMappedDtos() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Hammer");
        product.setAvailable(5);
        product.setPrice(BigDecimal.valueOf(19.99));

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDto> products = productService.getAllProducts();

        assertAll(
                () -> assertEquals(1, products.size(), "Should return one product"),
                () -> assertEquals("Hammer", products.get(0).getTitle(), "Title should be 'Hammer'")
        );
    }
}

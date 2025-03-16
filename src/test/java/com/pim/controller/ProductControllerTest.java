package com.pim.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.pim.controller.ProductController;
import com.pim.model.entity.Brand;
import com.pim.model.entity.Category;
import com.pim.model.entity.Product;
import com.pim.model.entity.Supplier;
import com.pim.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        Brand brand = new Brand();
        Supplier supplier = new Supplier();

        sampleProduct = new Product(UUID.randomUUID(), "TestSKU", category, brand, supplier);
    }


    @Test
    void testGetAllProducts() {
        List<Product> productList = Arrays.asList(sampleProduct);
        when(productService.getAllProducts()).thenReturn(productList);

        // Expect List<Product> directly, not ResponseEntity
        List<Product> response = productController.getAllProducts();

        assertThat(response).isNotNull().hasSize(1);
        assertThat(response.getFirst().getSku()).isEqualTo("TestSKU");
    }



    @Test
    void testGetProductById() {
        UUID productId = sampleProduct.getId();
        when(productService.getProductById(productId)).thenReturn(sampleProduct);

        Product response = productController.getProductById(productId);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(productId);
    }
}

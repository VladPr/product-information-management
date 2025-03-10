package com.pim.service;

import com.pim.model.Product;
import com.pim.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private UUID productId;

    @BeforeEach
    public void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setName(Map.of("en", "Test Product"));
        logger.info("Set up test data with productId: {}", productId);
    }

    @Test
    public void testGetAllProducts() {
        logger.info("Starting testGetAllProducts");
        when(productRepository.findAll()).thenReturn(List.of(product));
        List<Product> products = productService.getAllProducts();
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(product, products.get(0));
        logger.info("Finished testGetAllProducts");
    }

    @Test
    public void testGetProductById() {
        logger.info("Starting testGetProductById");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Product foundProduct = productService.getProductById(productId);
        assertNotNull(foundProduct);
        assertEquals(product, foundProduct);
        logger.info("Finished testGetProductById");
    }

    @Test
    public void testGetProductById_NotFound() {
        logger.info("Starting testGetProductById_NotFound");
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () ->
                productService.getProductById(productId)
        );
        assertEquals("Product not found", exception.getMessage());
        logger.info("Finished testGetProductById_NotFound");
    }

    @Test
    public void testCreateProduct() {
        logger.info("Starting testCreateProduct");
        when(productRepository.save(product)).thenReturn(product);
        Product createdProduct = productService.createProduct(product);
        assertNotNull(createdProduct);
        assertEquals(product, createdProduct);
        logger.info("Finished testCreateProduct");
    }

    @Test
    public void testUpdateProduct() {
        logger.info("Starting testUpdateProduct");
        String updatedName = "Updated Product";
        Product updatedProduct = new Product();
        updatedProduct.setName(Map.of("en", updatedName));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.updateProduct(productId, updatedProduct);

        assertNotNull(result);
        assertEquals(updatedName, result.getName().get("en"));

        verify(productRepository).findById(productId);
        assertEquals(updatedName, product.getName().get("en"));

        logger.info("Finished testUpdateProduct");
    }

    @Test
    public void testDeleteProduct() {
        logger.info("Starting testDeleteProduct");
        doNothing().when(productRepository).deleteById(productId);
        productService.deleteProduct(productId);
        verify(productRepository, times(1)).deleteById(productId);
        logger.info("Finished testDeleteProduct");
    }
}
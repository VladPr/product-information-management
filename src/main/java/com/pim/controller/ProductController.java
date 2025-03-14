package com.pim.controller;

import com.pim.model.dto.ProductDTO;
import com.pim.model.entity.Price;
import com.pim.model.entity.Product;
import com.pim.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/read/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/read/{id}")
    public Product getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PostMapping("/create")
    public List<Product> createProducts(@RequestBody List<ProductDTO> productDTOs) {
        return productService.saveAllProducts(productDTOs);
    }

    @PostMapping("/update/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/read/sku/{sku}")
    public Product getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku);
    }

    @GetMapping("/read/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable UUID categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/read/brand/{brandId}")
    public List<Product> getProductsByBrand(@PathVariable UUID brandId) {
        return productService.getProductsByBrandId(brandId);
    }

    @GetMapping("/read/supplier/{supplierId}")
    public List<Product> getProductsBySupplier(@PathVariable UUID supplierId) {
        return productService.getProductsBySupplierId(supplierId);
    }
}

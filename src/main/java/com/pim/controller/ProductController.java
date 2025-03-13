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

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PutMapping
    public Product createProduct(@RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }

    @PostMapping("/{id}")
    public Product updateProduct(@PathVariable UUID id, @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }

    @GetMapping ("/count")
    public Long countProducts() {
        return productService.countProducts();
    }

//    @PostMapping("/{id}/price")
//    public Product updateProductPrice(@PathVariable UUID id, @RequestBody Price price) {
//        return productService.updateProductPrice(id, price);
//    }

    @GetMapping("/sku/{sku}")
    public Product getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku);
    }

    @GetMapping("/category/{categoryId}")
    public List<Product> getProductsByCategoryId(@PathVariable UUID categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/brand/{brandId}")
    public List<Product> getProductsByBrand(@PathVariable UUID brandId) {
        return productService.getProductsByBrandId(brandId);
    }

    @GetMapping("/supplier/{supplierId}")
    public List<Product> getProductsBySupplier(@PathVariable UUID supplierId) {
        return productService.getProductsBySupplierId(supplierId);
    }

    @PostMapping ("/batch-upload")
    public List<Product> batchUploadProducts(@RequestBody List<ProductDTO> productsDTO) {
        return productService.saveAllProducts(productsDTO);
    }
}

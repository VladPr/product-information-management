package com.pim.service;

import com.pim.exception.ResourceNotFoundException;
import com.pim.model.dto.ProductDTO;
import com.pim.model.entity.*;
import com.pim.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          BrandRepository brandRepository,
                          SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(UUID id) {
        logger.info("Fetching product with id {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product getProductBySku(String sku) {
        logger.info("Fetching product with SKU {}", sku);
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    @Transactional
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        updateProductFromDTO(product, productDTO);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, ProductDTO productDTO) {
        Product existingProduct = getProductById(id);

        boolean isSkuChanged = !existingProduct.getSku().equals(productDTO.getSku());
        boolean isCategoryChanged = !existingProduct.getCategory().getName().equals(productDTO.getCategoryName());
        boolean isBrandChanged = !existingProduct.getBrand().getName().equals(productDTO.getBrandName());
        boolean isSupplierChanged = !existingProduct.getSupplier().getName().equals(productDTO.getSupplierName());
        boolean isNameTranslationsChanged = !existingProduct.getNameTranslations().equals(createNameTranslations(existingProduct, productDTO.getNameTranslations()));
        boolean isDescriptionChanged = !existingProduct.getDescriptionTranslations().equals(createDescriptionTranslations(existingProduct, productDTO.getDescription()));

        if (isSkuChanged || isCategoryChanged || isBrandChanged || isSupplierChanged || isNameTranslationsChanged || isDescriptionChanged) {
            updateProductFromDTO(existingProduct, productDTO);
            return productRepository.save(existingProduct);
        } else {
            logger.info("No changes detected for product with id: {}", id);
            return existingProduct;
        }
    }

    private void updateProductFromDTO(Product product, ProductDTO productDTO) {
        product.setSku(productDTO.getSku());

        Category category = getCategoryByName(productDTO.getCategoryName());
        Brand brand = getBrandByName(productDTO.getBrandName());
        Supplier supplier = getSupplierByName(productDTO.getSupplierName());

        logger.info("Setting category: {}", category.getName());
        logger.info("Setting brand: {}", brand.getName());
        logger.info("Setting supplier: {}", supplier.getName());

        product.setCategory(category);
        product.setBrand(brand);
        product.setSupplier(supplier);

        updateTranslations(product, productDTO);
    }

    private void updateTranslations(Product product, ProductDTO productDTO) {
        product.setNameTranslations(createNameTranslations(product, productDTO.getNameTranslations()));
        product.setDescriptionTranslations(createDescriptionTranslations(product, productDTO.getDescription()));
    }

    private Set<ProductNameTranslation> createNameTranslations(Product product, Map<String, String> translations) {
        return translations.entrySet().stream()
                .map(entry -> new ProductNameTranslation(entry.getKey(), entry.getValue(), product))
                .collect(Collectors.toSet());
    }

    private Set<ProductDescriptionTranslation> createDescriptionTranslations(Product product, Map<String, String> translations) {
        return translations.entrySet().stream()
                .map(entry -> new ProductDescriptionTranslation(entry.getKey(), entry.getValue(), product))
                .collect(Collectors.toSet());
    }

    private Category getCategoryByName(String name) {
        return categoryRepository.findByNameTranslations_NameTranslation(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + name));
    }

    private Brand getBrandByName(String name) {
        return brandRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + name));
    }

    private Supplier getSupplierByName(String name) {
        return supplierRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + name));
    }

    public void deleteProduct(UUID id) {
        logger.info("Deleting product with id {}", id);
        productRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllProducts() {
        logger.warn("Deleting all products from the database.");
        long productCount = productRepository.count();
        if (productCount == 0) {
            logger.warn("No products found to delete.");
            return;
        }
        productRepository.deleteAllInBatch();
        logger.info("Successfully deleted {} products from the database.", productCount);
    }

    public Long countProducts() {
        return productRepository.count();
    }

    public List<Product> getProductsByBrandId(UUID brandId) {
        return productRepository.findByBrandId(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("No products found for brand id " + brandId));
    }

    public List<Product> getProductsBySupplierId(UUID supplierId) {
        return productRepository.findBySupplierId(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("No products found for supplier id " + supplierId));
    }

    public List<Product> getProductsByCategoryId(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("No products found for category id " + categoryId));
    }

    @Transactional
    public List<Product> saveAllProducts(List<ProductDTO> productsDTO) {
        List<Product> products = productsDTO.stream()
                .map(this::createProduct)
                .collect(Collectors.toList());
        return productRepository.saveAll(products);
    }
}
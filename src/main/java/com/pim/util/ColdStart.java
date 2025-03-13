package com.pim.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pim.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class ColdStart {

    private final LanguageService languageService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final PriceService priceService;
    private final ProductService productService;
    private final SupplierService supplierService;
    private final WebClient webClient;
    private final String apiBaseUrl;

    public ColdStart(LanguageService languageService, CategoryService categoryService, BrandService brandService,
                     PriceService priceService, ProductService productService, SupplierService supplierService,
                     WebClient.Builder webClientBuilder, @Value("${api.base-url}") String apiBaseUrl) {
        this.languageService = languageService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.priceService = priceService;
        this.productService = productService;
        this.supplierService = supplierService;
        this.webClient = webClientBuilder.baseUrl(apiBaseUrl).build();
        this.apiBaseUrl = apiBaseUrl;
    }

    public void runColdStart() {
        deleteAllData();
        postTestData();
//        queryDatabaseCounts();
        System.out.println("Running ColdStart");
    }

    private void deleteAllData() {
        System.out.println("Deleting all data...");
        languageService.deleteAllLanguages();
        categoryService.deleteAllCategories();
        brandService.deleteAllBrands();
        priceService.deleteAllPrices();
        productService.deleteAllProducts();
        supplierService.deleteAllSuppliers();
    }

    private void postTestData() {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("cold-start-test/grocery_dataset.json");

        if (!resource.exists()) {
            throw new RuntimeException("Test dataset not found in classpath.");
        }

        try (InputStream inputStream = resource.getInputStream()) {
            Map<String, Object> data = mapper.readValue(inputStream, new TypeReference<>() {});

            postEntities(data.get("brands"), "/brands");
            postEntities(data.get("suppliers"), "/suppliers");
            postEntities(data.get("languages"), "/languages");
            postEntities(data.get("categories"), "/categories");
            postEntities(data.get("products"), "/products");
            postEntities(data.get("prices"), "/prices");

        } catch (IOException e) {
            throw new RuntimeException("Failed to read test dataset", e);
        }
    }

    private <T> void postEntities(Object entities, String uri) {
        if (!(entities instanceof List)) {
            throw new IllegalArgumentException("Invalid data format for " + uri);
        }

        List<T> entityList = new ObjectMapper().convertValue(entities, new TypeReference<List<T>>() {});
        for (T entity : entityList) {
            webClient.post().uri(uri)
                    .bodyValue(entity)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
    }

    private void queryDatabaseCounts() {
        queryTableCount("languages");
        queryTableCount("categories");
        queryTableCount("brands");
        queryTableCount("prices");
        queryTableCount("products");
        queryTableCount("suppliers");
    }

    private void queryTableCount(String tableName) {
        Long count = webClient.get()
                .uri("/{tableName}/count", tableName)
                .retrieve()
                .bodyToMono(Long.class)
                .block();

        System.out.println("Count for " + tableName + ": " + count);
    }
}

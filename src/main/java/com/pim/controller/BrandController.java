package com.pim.controller;

import com.pim.model.dto.BrandDTO;
import com.pim.model.entity.Brand;
import com.pim.model.entity.Product;
import com.pim.repository.BrandRepository;
import com.pim.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;
    private final BrandRepository brandRepository;

    public BrandController(BrandService brandService, BrandRepository brandRepository) {
        this.brandService = brandService;
        this.brandRepository = brandRepository;
    }

    @GetMapping
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/{id}")
    public Brand getBrandById(@PathVariable UUID id) {
        return brandService.getBrandById(id);
    }

    @PostMapping
    public Brand createBrand(@RequestBody BrandDTO brandDTO) {
        return brandService.createBrand(brandDTO);
    }

    @PutMapping("/{id}")
    public Brand updateBrand(@PathVariable UUID id, @RequestBody BrandDTO brandDTO) {
        return brandService.updateBrand(id, brandDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
    }

    @GetMapping("/count")
    public Long countBrands() {
        return brandService.countBrands();
    }

    @GetMapping("/{id}/products")
    public Optional<List<Product>> getProductsByBrand(@PathVariable UUID id) {
        return brandService.getProductsByBrandId(id);
    }

    @PostMapping("/batch-upload")
    public ResponseEntity<List<Brand>> batchUploadBrands(@RequestBody List<Brand> brands) {
        List<Brand> savedBrands = brandService.saveAllBrands(brands);
        return ResponseEntity.ok(savedBrands);
    }
}

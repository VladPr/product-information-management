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

    public BrandController(BrandService brandService, BrandRepository brandRepository) {
        this.brandService = brandService;
    }

    @GetMapping("/read/all")
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @GetMapping("/read/{id}")
    public Brand getBrandById(@PathVariable UUID id) {
        return brandService.getBrandById(id);
    }

    @GetMapping("/read/{id}/products")
    public Optional<List<Product>> getProductsByBrand(@PathVariable UUID id) {
        return brandService.getProductsByBrandId(id);
    }

    @PostMapping("/create")
    public ResponseEntity<List<Brand>> createBrands(@RequestBody List<BrandDTO> brandDTOs) {
        List<Brand> savedBrands = brandService.createBrands(brandDTOs);
        return ResponseEntity.ok(savedBrands);
    }

    @PutMapping("/update/{id}")
    public Brand updateBrand(@PathVariable UUID id, @RequestBody BrandDTO brandDTO) {
        return brandService.updateBrand(id, brandDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
    }

    @DeleteMapping("/delete/delete-all")
    public void deleteAll() {
        brandService.deleteAllBrands();
    }
}

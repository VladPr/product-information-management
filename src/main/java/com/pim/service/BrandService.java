package com.pim.service;

import com.pim.model.Brand;
import com.pim.repository.BrandRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
    }

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand updateBrand(UUID id, Brand updatedBrand) {
        Brand existingBrand = getBrandById(id);
        existingBrand.setName(updatedBrand.getName());
        return brandRepository.save(existingBrand);
    }

    public void deleteBrand(UUID id) {
        brandRepository.deleteById(id);
    }
}
package com.pim.service;

import com.pim.model.dto.BrandDTO;
import com.pim.model.entity.Brand;
import com.pim.model.entity.Product;
import com.pim.repository.BrandRepository;
import com.pim.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(BrandService.class);

    public BrandService(BrandRepository brandRepository, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
    }

    public List<Brand> getAllBrands() {
        logger.info("Fetching all brands from the database.");
        return brandRepository.findAll();
    }

    public Brand getBrandById(UUID id) {
        logger.info("Fetching brand with id: {}", id);
        return brandRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Brand with id {} not found.", id);
                    return new EntityNotFoundException("Brand with id " + id + " not found.");
                });
    }

    @Transactional
    public List<Brand> createBrands(List<BrandDTO> brandDTOs) {
        logger.info("Attempting to create or update brands.");

        List<Brand> brands = brandDTOs.stream().map(brandDTO -> {
            if (brandDTO.getName() == null || brandDTO.getName().trim().isEmpty()) {
                throw new RuntimeException("Brand name cannot be null or empty");
            }

            Optional<Brand> existingBrand = brandRepository.findByName(brandDTO.getName());
            if (existingBrand.isPresent()) {
                logger.info("Updating existing brand: {}", brandDTO.getName());
                return updateBrand(existingBrand.get().getId(), brandDTO);
            } else {
                logger.info("Creating new brand: {}", brandDTO.getName());
                Brand brand = new Brand();
                brand.setName(brandDTO.getName());
                return brand;
            }
        }).collect(Collectors.toList());

        return brandRepository.saveAll(brands);
    }

    @Transactional
    public Brand updateBrand(UUID id, BrandDTO brandDTO) {
        logger.info("Updating brand with id: {}", id);

        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Attempted to update a non-existent brand with id: {}", id);
                    return new EntityNotFoundException("Brand with id " + id + " not found.");
                });

        existingBrand.setName(brandDTO.getName());

        return brandRepository.save(existingBrand);
    }

    @Transactional
    public void deleteBrand(UUID id) {
        logger.info("Deleting brand with id: {}", id);

        if (!brandRepository.existsById(id)) {
            logger.warn("Attempted to delete a non-existent brand with id: {}", id);
            return;  // Return without throwing an exception
        }

        brandRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllBrands() {
        logger.warn("Deleting all brands from the database.");

        // Optional: Check if there are brands to delete
        long brandCount = brandRepository.count();
        if (brandCount == 0) {
            logger.warn("No brands found to delete.");
            return;
        }

        try {
            // Perform batch deletion of all brands
            brandRepository.deleteAllInBatch();
            logger.info("Successfully deleted all brands from the database.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting all brands: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete all brands", e);  // Rethrow or handle accordingly
        }
    }

    public Optional<List<Product>> getProductsByBrandId(UUID brandId) {
        Optional<List<Product>> products = productRepository.findByBrandId(brandId);
        if (products.isEmpty()) {
            logger.warn("No products found for brand with id {}", brandId);
            throw new EntityNotFoundException("No products found for brand id " + brandId);
        }
        return products;
    }

    @Transactional
    public List<Brand> saveAllBrands(List<Brand> brands) {
        return brandRepository.saveAll(brands);
    }
}
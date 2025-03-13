package com.pim.service;

import com.pim.model.dto.BrandDTO;
import com.pim.model.entity.Brand;
import com.pim.repository.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(BrandServiceTest.class);

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;
    private BrandDTO brandDTO;
    private UUID brandId;

    @BeforeEach
    public void setUp() {
        brandId = UUID.randomUUID();
        brand = new Brand();
        brand.setId(brandId);
        brand.setName("Test Brand");

        brandDTO = new BrandDTO();
        brandDTO.setName("Test Brand");

        logger.info("Set up test data with brandId: {}", brandId);
    }

    @Test
    public void testGetAllBrands() {
        logger.info("Starting testGetAllBrands");
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        List<Brand> brands = brandService.getAllBrands();
        assertNotNull(brands);
        assertEquals(1, brands.size());
        assertEquals(brand, brands.get(0));
        logger.info("Finished testGetAllBrands");
    }

    @Test
    public void testGetBrandById() {
        logger.info("Starting testGetBrandById");
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        Brand foundBrand = brandService.getBrandById(brandId);
        assertNotNull(foundBrand);
        assertEquals(brand, foundBrand);
        logger.info("Finished testGetBrandById");
    }

    @Test
    public void testGetBrandById_NotFound() {
        logger.info("Starting testGetBrandById_NotFound");
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                brandService.getBrandById(brandId)
        );
        assertEquals("Brand with id " + brandId + " not found.", exception.getMessage());
        logger.info("Finished testGetBrandById_NotFound");
    }

    @Test
    public void testCreateBrand() {
        logger.info("Starting testCreateBrand");
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);
        Brand createdBrand = brandService.createBrand(brandDTO);
        assertNotNull(createdBrand);
        assertEquals(brand.getName(), createdBrand.getName());
        logger.info("Finished testCreateBrand");
    }

    @Test
    public void testUpdateBrand() {
        logger.info("Starting testUpdateBrand");
        String updatedName = "Updated Brand";
        brandDTO.setName(updatedName);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(brandRepository.save(brand)).thenReturn(brand);

        Brand result = brandService.updateBrand(brandId, brandDTO);

        assertNotNull(result);
        assertEquals(updatedName, result.getName());

        verify(brandRepository).findById(brandId);
        assertEquals(updatedName, brand.getName());

        logger.info("Finished testUpdateBrand");
    }

    @Test
    public void testDeleteBrand() {
        logger.info("Starting testDeleteBrand");
        when(brandRepository.existsById(brandId)).thenReturn(false);
        brandService.deleteBrand(brandId);
        verify(brandRepository, times(1)).existsById(brandId);
        verify(brandRepository, times(0)).deleteById(brandId);
        logger.info("Finished testDeleteBrand");
    }
}
package com.pim.service;

import com.pim.model.entity.Brand;
import com.pim.repository.BrandRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    private Brand brand;
    private UUID brandId;

    @BeforeEach
    void setUp() {
        brandId = UUID.randomUUID();
        brand = new Brand();
        brand.setId(brandId);
        brand.setName("Test Brand");
    }

    @Test
    void testGetAllBrands() {
        when(brandRepository.findAll()).thenReturn(List.of(brand));

        List<Brand> brands = brandService.getAllBrands();
        assertNotNull(brands);
        assertEquals(1, brands.size());
    }

    @Test
    void testGetBrandById_Success() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        Brand foundBrand = brandService.getBrandById(brandId);
        assertNotNull(foundBrand);
        assertEquals("Test Brand", foundBrand.getName());
    }

    @Test
    void testGetBrandById_NotFound() {
        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.getBrandById(brandId));
    }
}

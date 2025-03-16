package com.pim.service;

import com.pim.exception.ResourceNotFoundException;
import com.pim.model.dto.PriceDTO;
import com.pim.model.entity.Price;
import com.pim.model.entity.Product;
import com.pim.repository.PriceRepository;
import com.pim.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PriceService priceService;

    private Price price;
    private UUID priceId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        priceId = UUID.randomUUID();
        productId = UUID.randomUUID();
        price = new Price();
        price.setId(priceId);
        price.setProductId(productId);
    }

    @Test
    void testGetPriceById_Success() {
        when(priceRepository.findById(priceId)).thenReturn(Optional.of(price));

        Price foundPrice = priceService.getPriceById(priceId);
        assertNotNull(foundPrice);
        assertEquals(priceId, foundPrice.getId());
    }

    @Test
    void testGetPriceById_NotFound() {
        when(priceRepository.findById(priceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> priceService.getPriceById(priceId));
    }

    @Test
    void testDeletePrice_Success() {
        when(priceRepository.existsById(priceId)).thenReturn(true);
        doNothing().when(priceRepository).deleteById(priceId);

        priceService.deletePrice(priceId);

        verify(priceRepository, times(1)).deleteById(priceId);
    }

    @Test
    void testDeletePrice_NotFound() {
        when(priceRepository.existsById(priceId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> priceService.deletePrice(priceId));
    }
}

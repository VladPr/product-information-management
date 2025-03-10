package com.pim.service;

import com.pim.model.Price;
import com.pim.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(PriceServiceTest.class);

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private Price price;
    private UUID priceId;

    @BeforeEach
    public void setUp() {
        priceId = UUID.randomUUID();
        price = new Price();
        price.setId(priceId);
        price.setAmount(BigDecimal.valueOf(100.0));
        logger.info("Set up test data with priceId: {}", priceId);
    }

    @Test
    public void testGetAllPrices() {
        logger.info("Starting testGetAllPrices");
        when(priceRepository.findAll()).thenReturn(List.of(price));
        List<Price> prices = priceService.getAllPrices();
        assertNotNull(prices);
        assertEquals(1, prices.size());
        assertEquals(price, prices.get(0));
        logger.info("Finished testGetAllPrices");
    }

    @Test
    public void testGetPriceById() {
        logger.info("Starting testGetPriceById");
        when(priceRepository.findById(priceId)).thenReturn(Optional.of(price));
        Price foundPrice = priceService.getPriceById(priceId);
        assertNotNull(foundPrice);
        assertEquals(price, foundPrice);
        logger.info("Finished testGetPriceById");
    }

    @Test
    public void testGetPriceById_NotFound() {
        logger.info("Starting testGetPriceById_NotFound");
        when(priceRepository.findById(priceId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () ->
                priceService.getPriceById(priceId)
        );
        assertEquals("Price not found", exception.getMessage());
        logger.info("Finished testGetPriceById_NotFound");
    }

    @Test
    public void testCreatePrice() {
        logger.info("Starting testCreatePrice");
        when(priceRepository.save(price)).thenReturn(price);
        Price createdPrice = priceService.createPrice(price);
        assertNotNull(createdPrice);
        assertEquals(price, createdPrice);
        logger.info("Finished testCreatePrice");
    }

    @Test
    public void testUpdatePrice() {
        logger.info("Starting testUpdatePrice");
        BigDecimal updatedAmount = BigDecimal.valueOf(150.0);
        Price updatedPrice = new Price();
        updatedPrice.setAmount(updatedAmount);

        when(priceRepository.findById(priceId)).thenReturn(Optional.of(price));
        when(priceRepository.save(price)).thenReturn(price);

        Price result = priceService.updatePrice(priceId, updatedPrice);

        assertNotNull(result);
        assertEquals(updatedAmount, result.getAmount());

        verify(priceRepository).findById(priceId);
        assertEquals(updatedAmount, price.getAmount());

        logger.info("Finished testUpdatePrice");
    }

    @Test
    public void testDeletePrice() {
        logger.info("Starting testDeletePrice");
        doNothing().when(priceRepository).deleteById(priceId);
        priceService.deletePrice(priceId);
        verify(priceRepository, times(1)).deleteById(priceId);
        logger.info("Finished testDeletePrice");
    }
}
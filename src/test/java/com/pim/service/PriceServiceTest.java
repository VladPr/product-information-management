package com.pim.service;

import com.pim.model.dto.PriceDTO;
import com.pim.model.entity.Price;
import com.pim.model.entity.PriceAmount;
import com.pim.model.entity.Product;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(PriceServiceTest.class);

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private PriceService priceService;

    private Price price;
    private UUID priceId;

    @BeforeEach
    public void setUp() {
        priceId = UUID.randomUUID();
        price = new Price();
        price.setId(priceId);

        PriceAmount priceAmount = new PriceAmount();
        priceAmount.setCurrencyCode("EUR");
        priceAmount.setAmount(BigDecimal.valueOf(100.0));
        priceAmount.setPrice(price);

        price.setAmounts(List.of(priceAmount));
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

        PriceDTO priceDTO = new PriceDTO();
        priceDTO.setAmount(Map.of("EUR", BigDecimal.valueOf(100.0)));
        priceDTO.setProductSku("some-sku");

        when(priceRepository.save(any(Price.class))).thenReturn(price);
        when(productService.getProductBySku("some-sku")).thenReturn(new Product());

        Price createdPrice = priceService.createPrice(priceDTO);

        assertNotNull(createdPrice);
        assertEquals(price.getAmounts().get(0).getAmount(), createdPrice.getAmounts().get(0).getAmount());

        logger.info("Finished testCreatePrice");
    }

    @Test
    public void testUpdatePrice() {
        logger.info("Starting testUpdatePrice");
        BigDecimal updatedAmount = BigDecimal.valueOf(150.0);
        String currencyCode = "EUR";

        PriceAmount updatedPriceAmount = new PriceAmount();
        updatedPriceAmount.setCurrencyCode(currencyCode);
        updatedPriceAmount.setAmount(updatedAmount);

        Price updatedPrice = new Price();
        updatedPrice.setAmounts(List.of(updatedPriceAmount));

        when(priceRepository.findById(priceId)).thenReturn(Optional.of(price));
        when(priceRepository.save(any(Price.class))).thenReturn(price);

        PriceDTO priceDTO = new PriceDTO();
        priceDTO.setAmount(Map.of(currencyCode, updatedAmount));
        priceDTO.setProductSku("some-sku");

        Price result = priceService.updatePrice(priceId, priceDTO);

        assertNotNull(result);
        assertEquals(updatedAmount, result.getAmounts().get(0).getAmount());

        verify(priceRepository).findById(priceId);
        assertEquals(updatedAmount, price.getAmounts().get(0).getAmount());

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

    @Test
    public void testDeleteAllPrices() {
        logger.info("Starting testDeleteAllPrices");
        when(priceRepository.count()).thenReturn(1L);
        doNothing().when(priceRepository).deleteAllInBatch();

        priceService.deleteAllPrices();

        verify(priceRepository, times(1)).count();
        verify(priceRepository, times(1)).deleteAllInBatch();
        logger.info("Finished testDeleteAllPrices");
    }
}
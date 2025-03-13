package com.pim.service;

import com.pim.model.dto.PriceDTO;
import com.pim.model.entity.Price;
import com.pim.model.entity.PriceAmount;
import com.pim.model.entity.Product;
import com.pim.repository.PriceRepository;
import com.pim.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final ProductService productService;

    public PriceService(PriceRepository priceRepository, ProductService productService) {
        this.priceRepository = priceRepository;
        this.productService = productService;
    }

    @Transactional
    public List<Price> getAllPrices() {
        logger.info("Fetching all prices");
        return priceRepository.findAll();
    }

    @Transactional
    public Price getPriceById(UUID id) {
        logger.info("Fetching price with id: {}", id);
        return priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found with id: " + id));
    }

    @Transactional
    public Price createPrice(PriceDTO priceDTO) {
        logger.info("Creating new price: {}", priceDTO);

        if (priceDTO.getAmount() == null) {
            throw new ConstraintViolationException("Price amount cannot be null", null);
        }

        // Check if price for this product already exists
        Price existingPrice = priceRepository.findByProductSku(priceDTO.getProductSku());
        if (existingPrice != null) {
            logger.info("Price already exists for product SKU: {}. Using update operation.", priceDTO.getProductSku());
            return updatePriceByProductSku(priceDTO.getProductSku(), priceDTO);
        }

        // Create a new price
        Price price = convertDtoToEntity(priceDTO);
        return priceRepository.save(price);
    }

    @Transactional
    public Price updatePriceByProductSku(String productSku, PriceDTO priceDTO) {
        Price existingPrice = priceRepository.findByProductSku(productSku);
        if (existingPrice == null) {
            throw new ResourceNotFoundException("Price not found for product SKU: " + productSku);
        }

        Price updatedPrice = convertDtoToEntity(priceDTO);
        updatedPrice.setId(existingPrice.getId());
        return priceRepository.save(updatedPrice);
    }

    @Transactional
    public Price updatePrice(UUID id, PriceDTO priceDTO) {
        logger.info("Updating price with id: {}", id);

        Price existingPrice = priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price with id " + id + " not found."));

        Price updatedPrice = convertDtoToEntity(priceDTO);
        updatedPrice.setId(existingPrice.getId());

        return priceRepository.save(updatedPrice);
    }

    @Transactional
    public void deletePrice(UUID id) {
        logger.info("Deleting price with id: {}", id);

        if (!priceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete - price with id " + id + " not found");
        }

        priceRepository.deleteById(id);
    }

    @Transactional
    public Long countPrices() {
        logger.info("Counting all prices");
        return priceRepository.count();
    }

    @Transactional
    public void deleteAllPrices() {
        logger.warn("Deleting all prices from the database.");

        long priceCount = priceRepository.count();
        if (priceCount == 0) {
            logger.warn("No prices found to delete.");
            return;
        }

        try {
            priceRepository.deleteAllInBatch();
            logger.info("Successfully deleted all prices from the database.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting all prices: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete all prices", e);
        }
    }

    private Price convertDtoToEntity(PriceDTO priceDTO) {
        Price price = new Price();

        // Validate productSku first to fail fast
        if (priceDTO.getProductSku() == null) {
            throw new ConstraintViolationException("Product SKU cannot be null", null);
        }

        // Find and set the product
        Product product = productService.getProductBySku(priceDTO.getProductSku());
        price.setProduct(product);

        // Create and associate price amounts
        List<PriceAmount> amounts = priceDTO.getAmount().entrySet().stream()
                .map(entry -> {
                    PriceAmount priceAmount = new PriceAmount();
                    priceAmount.setCurrencyCode(entry.getKey());
                    priceAmount.setAmount(entry.getValue());
                    priceAmount.setPrice(price);
                    return priceAmount;
                })
                .collect(Collectors.toList());
        price.setAmounts(amounts);

        return price;
    }

    @Transactional
    public List<Price> saveAllPrices(List<PriceDTO> priceDTOs) {
        logger.info("Saving all prices");
        List<Price> prices = priceDTOs.stream()
                .map(this::convertDtoToEntity)
                .collect(Collectors.toList());
        return priceRepository.saveAll(prices);
    }
}
package com.pim.service;

import com.pim.model.dto.PriceDTO;
import com.pim.model.entity.Price;
import com.pim.model.entity.PriceAmount;
import com.pim.model.entity.Product;
import com.pim.repository.PriceRepository;
import com.pim.exception.ResourceNotFoundException;
import com.pim.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final ProductService productService;

    public PriceService(PriceRepository priceRepository, ProductService productService, ProductRepository productRepository) {
        this.priceRepository = priceRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Transactional
    public List<Price> getAllPrices() {
        logger.info("Fetching all prices");
        return priceRepository.findAll();
    }

    @Transactional
    public Price getPriceById(UUID id) {
        logger.info("Fetching price with id: {}", id);
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price not found with id: " + id));
        logger.info("Price found. Amounts are {}", price.getAmounts());
        return price;
    }

    @Transactional
    public Price createPrice(PriceDTO priceDTO) {
        logger.info("Creating new price: {}", priceDTO);

        if (priceDTO.getAmount() == null) {
            throw new ConstraintViolationException("Price amount cannot be null", null);
        }

        // Fetch product by SKU
        Product product = productRepository.findBySku(priceDTO.getProductSku())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + priceDTO.getProductSku()));

        // Check if price for this product already exists
        Price existingPrice = priceRepository.findByProductId(product.getId());
        if (existingPrice != null) {
            logger.info("Price already exists for product SKU: {}. Using update operation.", priceDTO.getProductSku());
            return updatePriceByProductSku(product.getSku(), priceDTO);
        }

        // Create a new price
        Price price = convertDtoToEntity(priceDTO);
        price.setProductId(product.getId());
        return priceRepository.save(price);
    }

    @Transactional
    public Price updatePrice(UUID id, PriceDTO priceDTO) {
        logger.info("Updating price with id: {}", id);

        Price existingPrice = priceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Price with id " + id + " not found."));

        Price updatedPrice = updatePriceFromPriceDTO(existingPrice, priceDTO);

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


    private Price updatePriceFromPriceDTO(Price existingPrice, PriceDTO priceDTO) {

        boolean isAmountChanged = !existingPrice.getAmounts().stream()
                .collect(Collectors.toMap(PriceAmount::getCurrencyCode, PriceAmount::getAmount))
                .equals(priceDTO.getAmount());

        if (!isAmountChanged) {
            logger.info("No changes detected for price with id: {}", existingPrice.getId());
            return existingPrice;
        } else {
            logger.info("Updating price with id: {}", existingPrice.getId());
            Price updatedPrice = new Price();
            updatedPrice.setProductId(existingPrice.getProductId());
            updatedPrice.setAmounts(priceDTO.getAmount().entrySet().stream()
                    .map(entry -> {
                        PriceAmount priceAmount = new PriceAmount();
                        priceAmount.setCurrencyCode(entry.getKey());
                        priceAmount.setAmount(entry.getValue());
                        priceAmount.setPrice(updatedPrice);
                        return priceAmount;
                    })
                    .collect(Collectors.toList()));
            return priceRepository.save(updatedPrice);
        }
    }

    private Price convertDtoToEntity(PriceDTO priceDTO) {
        Price price = new Price();

        // Validate productSku first to fail fast
        if (priceDTO.getProductSku() == null) {
            throw new ConstraintViolationException("Product SKU cannot be null", null);
        }

        // Fetch product by SKU
        Product product = productRepository.findBySku(priceDTO.getProductSku())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + priceDTO.getProductSku()));
        price.setProductId(product.getId());

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

    public Price getPriceByProductId(UUID productId) {
        logger.info("Fetching price by product id: {}", productId);

        return priceRepository.findByProductId(productId);
    }

    @Transactional
    public Price updatePriceByProductSku(String productSku, PriceDTO priceDTO) {
        logger.info("Updating price for product SKU: {}", productSku);

        // Fetch product by SKU
        Product product = productRepository.findBySku(productSku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + productSku));

        // Check if price for this product already exists
        Price existingPrice = priceRepository.findByProductId(product.getId());
        if (existingPrice == null) {
            throw new ResourceNotFoundException("Price not found for product SKU: " + productSku);
        }

        // Update the existing price
        Price updatedPrice = updatePriceFromPriceDTO(existingPrice, priceDTO);
        return priceRepository.save(updatedPrice);
    }


    @Transactional
    public List<Price> createPrices(List<PriceDTO> priceDTOs) {
        if (priceDTOs == null || priceDTOs.isEmpty()) {
            throw new IllegalArgumentException("Price data cannot be empty");
        } else {
            return priceDTOs.stream()
                    .map(this::createPrice)
                    .collect(Collectors.toList());
        }
    }
}
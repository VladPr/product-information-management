package com.pim.service;

import com.pim.model.Price;
import com.pim.repository.PriceRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public List<Price> getAllPrices() {
        return priceRepository.findAll();
    }

    public Price getPriceById(UUID id) {
        return priceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Price not found"));
    }

    public Price createPrice(Price price) {
        return priceRepository.save(price);
    }

    public Price updatePrice(UUID id, Price updatedPrice) {
        Price existingPrice = getPriceById(id);
        existingPrice.setAmount(updatedPrice.getAmount());
        existingPrice.setValidFrom(updatedPrice.getValidFrom());
        existingPrice.setValidTo(updatedPrice.getValidTo());
        return priceRepository.save(existingPrice);
    }

    public void deletePrice(UUID id) {
        priceRepository.deleteById(id);
    }
}
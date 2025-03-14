package com.pim.controller;

import com.pim.model.dto.PriceDTO;
import com.pim.model.entity.Price;
import com.pim.service.PriceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/read/all")
    public List<Price> getAllPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping("/read/{id}")
    public Price getPriceById(@PathVariable UUID id) {
        return priceService.getPriceById(id);
    }

    @GetMapping("/read/product-price/{productId}")
    public Price getPriceByProductId(@PathVariable UUID productId) {
        return priceService.getPriceByProductId(productId);
    }

    @PostMapping("/create")
    public List<Price> createPrices(@RequestBody List<PriceDTO> priceDTOs) {
        return priceService.createPrices(priceDTOs);
    }

    @PutMapping("/update/{id}")
    public Price updatePrice(@PathVariable UUID id, @RequestBody PriceDTO priceDTO) {
        return priceService.updatePrice(id, priceDTO);
    }

    @PostMapping("/update/product-sku/{productSku}")
    public Price updatePriceByProductSku(@PathVariable String productSku, @RequestBody PriceDTO priceDTO) {
        return priceService.updatePriceByProductSku(productSku, priceDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deletePrice(@PathVariable UUID id) {
        priceService.deletePrice(id);
    }

    @DeleteMapping("/delete/delete-all")
    public void deleteAllPrices() {
        priceService.deleteAllPrices();
    }
}
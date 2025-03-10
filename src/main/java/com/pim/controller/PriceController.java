package com.pim.controller;

import com.pim.model.Price;
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

    @GetMapping
    public List<Price> getAllPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping("/{id}")
    public Price getPriceById(@PathVariable UUID id) {
        return priceService.getPriceById(id);
    }

    @PostMapping
    public Price createPrice(@RequestBody Price price) {
        return priceService.createPrice(price);
    }

    @PutMapping("/{id}")
    public Price updatePrice(@PathVariable UUID id, @RequestBody Price price) {
        return priceService.updatePrice(id, price);
    }

    @DeleteMapping("/{id}")
    public void deletePrice(@PathVariable UUID id) {
        priceService.deletePrice(id);
    }
}
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

    @GetMapping
    public List<Price> getAllPrices() {
        return priceService.getAllPrices();
    }

    @GetMapping("/{id}")
    public Price getPriceById(@PathVariable UUID id) {
        return priceService.getPriceById(id);
    }

    @PostMapping
    public Price createPrice(@RequestBody PriceDTO priceDTO) {
        return priceService.createPrice(priceDTO);
    }

    @PutMapping("/{id}")
    public Price updatePrice(@PathVariable UUID id, @RequestBody PriceDTO priceDTO) {
        return priceService.updatePrice(id, priceDTO);
    }

    @DeleteMapping("/{id}")
    public void deletePrice(@PathVariable UUID id) {
        priceService.deletePrice(id);
    }

    @DeleteMapping
    public void deleteAllPrices() {
        priceService.deleteAllPrices();
    }

    @GetMapping("/count")
    public Long countPrices() {
        return priceService.countPrices();
    }

    @PostMapping ("/batch-upload")
    public List<Price> batchUploadPrices(@RequestBody List<PriceDTO> pricesDTO) {
        return priceService.saveAllPrices(pricesDTO);
    }
}
package com.pim.controller;

import com.pim.model.dto.SupplierDTO;
import com.pim.model.entity.Product;
import com.pim.model.entity.Supplier;
import com.pim.service.ProductService;
import com.pim.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final ProductService productService;

    public SupplierController(SupplierService supplierService, ProductService productService) {
        this.supplierService = supplierService;
        this.productService = productService;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable UUID id) {
        return supplierService.getSupplierById(id);
    }


    @PostMapping
    public Supplier createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return supplierService.createSupplier(supplierDTO);
    }

    @PutMapping("/{id}")
    public Supplier updateSupplier(@PathVariable UUID id, @RequestBody SupplierDTO supplierDTO) {
        return supplierService.updateSupplier(id, supplierDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
    }

    @DeleteMapping
    public void deleteAllSuppliers() {
        supplierService.deleteAllSuppliers();
    }

    @GetMapping("/count")
    public Long countSuppliers() {
        return supplierService.countSuppliers();
    }

    @GetMapping("/{id}/products")
    public List<Product> getProductsBySupplier(@PathVariable UUID id) {
        return productService.getProductsBySupplierId(id);
    }

    // Save all suppliers
    @PostMapping("/batch-upload")
    public List<Supplier> batchUploadSuppliers(@RequestBody List<SupplierDTO> suppliersDTO) {
        return supplierService.saveAllSuppliers(suppliersDTO);
    }
}
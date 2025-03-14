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

    @GetMapping("/read/all")
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/read/{id}")
    public Supplier getSupplierById(@PathVariable UUID id) {
        return supplierService.getSupplierById(id);
    }

    @PostMapping("/create")
    public List<Supplier> createSuppliers(@RequestBody List<SupplierDTO> supplierDTOs) {
        return supplierService.createSuppliers(supplierDTOs);
    }

    @PutMapping("/update/{id}")
    public Supplier updateSupplier(@PathVariable UUID id, @RequestBody SupplierDTO supplierDTO) {
        return supplierService.updateSupplier(id, supplierDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSupplier(@PathVariable UUID id) {
        supplierService.deleteSupplier(id);
    }

    @DeleteMapping("/delete/delete-all")
    public void deleteAllSuppliers() {
        supplierService.deleteAllSuppliers();
    }

//    @GetMapping("/read/{id}/products")
//    public List<Product> getProductsBySupplier(@PathVariable UUID id) {
//        return productService.getProductsBySupplierId(id);
//    }

}
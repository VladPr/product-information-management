package com.pim.service;

import com.pim.model.dto.SupplierDTO;
import com.pim.model.entity.Product;
import com.pim.model.entity.Supplier;
import com.pim.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        logger.info("Fetching all suppliers from the database.");
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(UUID id) {
        logger.info("Fetching supplier with id: {}", id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Supplier with id {} not found.", id);
                    return new EntityNotFoundException("Supplier with id " + id + " not found.");
                });
    }

    public Supplier createSupplier(SupplierDTO supplierDTO) {
        logger.info("Attempting to create a new supplier: {}", supplierDTO);

        if (supplierDTO.getName() == null || supplierDTO.getName().trim().isEmpty()) {
            throw new ConstraintViolationException("Supplier name cannot be null or empty", null);
        }

        Optional<Supplier> existingSupplier = supplierRepository.findByName(supplierDTO.getName());
        if (existingSupplier.isPresent()) {
            logger.warn("Duplicate supplier detected: {}", supplierDTO.getName());
            throw new DataIntegrityViolationException("Supplier with name '" + supplierDTO.getName() + "' already exists.");
        }

        Supplier supplier = convertToEntity(supplierDTO);
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(UUID id, SupplierDTO supplierDTO) {
        logger.info("Updating supplier with id: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with id " + id + " not found."));

        existingSupplier.setName(supplierDTO.getName());

        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(UUID id) {
        logger.info("Attempting to delete supplier with id: {}", id);

        if (!supplierRepository.existsById(id)) {
            logger.warn("Attempted to delete a non-existent supplier with id: {}", id);
            throw new EntityNotFoundException("Supplier with id " + id + " not found.");
        }

        supplierRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllSuppliers() {
        logger.warn("Deleting all suppliers from the database.");

        // Optional: Check if there are suppliers to delete
        long supplierCount = supplierRepository.count();
        if (supplierCount == 0) {
            logger.warn("No suppliers found to delete.");
            return;
        }

        try {
            supplierRepository.deleteAllInBatch();
            logger.info("Successfully deleted all suppliers from the database.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting all suppliers: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete all suppliers", e);  // Rethrow or handle accordingly
        }
    }

    public Long countSuppliers() {
        long count = supplierRepository.count();
        logger.info("Total number of suppliers: {}", count);
        return count;
    }

    // Get all products by supplier ID
    List <Product> getProductsBySupplierId(UUID id) {
        logger.info("Fetching all products by supplier id: {}", id);
        return supplierRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Supplier with id {} not found.", id);
                    return new EntityNotFoundException("Supplier with id " + id + " not found.");
                }).getProducts();
    }


    private Supplier convertToEntity(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierDTO.getName());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber());
        supplier.setContactEmail(supplierDTO.getContactEmail());

        return supplier;
    }

    // Save all suppliers
    public List<Supplier> saveAllSuppliers(List<SupplierDTO> supplierDTOs) {
        logger.info("Saving all suppliers");
        List<Supplier> suppliers = supplierDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        return supplierRepository.saveAll(suppliers);
    }

}
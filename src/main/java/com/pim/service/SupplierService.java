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

    @Transactional
    public List<Supplier> createSuppliers(List<SupplierDTO> supplierDTOs) {
        logger.info("Attempting to create or update suppliers.");

        List<Supplier> suppliers = supplierDTOs.stream().map(supplierDTO -> {
            if (supplierDTO.getName() == null || supplierDTO.getName().trim().isEmpty()) {
                throw new RuntimeException("Supplier name cannot be null or empty");
            }

            Optional<Supplier> existingSupplier = supplierRepository.findByName(supplierDTO.getName());
            if (existingSupplier.isPresent()) {
                logger.info("Updating existing supplier: {}", supplierDTO.getName());
                return updateSupplier(existingSupplier.get().getId(), supplierDTO);
            } else {
                logger.info("Creating new supplier: {}", supplierDTO.getName());
                Supplier supplier = new Supplier();
                supplier.setName(supplierDTO.getName());
                supplier.setAddress(supplierDTO.getAddress());
                supplier.setPhoneNumber(supplierDTO.getPhoneNumber());
                supplier.setContactEmail(supplierDTO.getContactEmail());

                return supplierRepository.save(supplier);
            }
        }).collect(Collectors.toList());

        return suppliers;
    }

    @Transactional
    public Supplier updateSupplier(UUID id, SupplierDTO supplierDTO) {
        logger.info("Updating supplier with id: {}", id);

        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier with id " + id + " not found."));

        boolean isNameChanged = !existingSupplier.getName().equals(supplierDTO.getName());
        boolean isAddressChanged = !existingSupplier.getAddress().equals(supplierDTO.getAddress());
        boolean isPhoneNumberChanged = !existingSupplier.getPhoneNumber().equals(supplierDTO.getPhoneNumber());
        boolean isContactEmailChanged = !existingSupplier.getContactEmail().equals(supplierDTO.getContactEmail());

        if (!isNameChanged && !isAddressChanged && !isPhoneNumberChanged && !isContactEmailChanged) {
            logger.info("No changes detected for supplier with id: {}", id);
            return existingSupplier;
        }

        if (isNameChanged) {
            existingSupplier.setName(supplierDTO.getName());
        }
        if (isAddressChanged) {
            existingSupplier.setAddress(supplierDTO.getAddress());
        }
        if (isPhoneNumberChanged) {
            existingSupplier.setPhoneNumber(supplierDTO.getPhoneNumber());
        }
        if (isContactEmailChanged) {
            existingSupplier.setContactEmail(supplierDTO.getContactEmail());
        }

        return supplierRepository.save(existingSupplier);
    }

    @Transactional
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
            throw new RuntimeException("Failed to delete all suppliers", e);
        }
    }

//    public Long countSuppliers() {
//        long count = supplierRepository.count();
//        logger.info("Total number of suppliers: {}", count);
//        return count;
//    }

//    public List<Product> getProductsBySupplierId(UUID id) {
//        logger.info("Fetching all products by supplier id: {}", id);
//        return supplierRepository.findById(id)
//                .orElseThrow(() -> {
//                    logger.warn("Supplier with id {} not found.", id);
//                    return new EntityNotFoundException("Supplier with id " + id + " not found.");
//                }).getProducts();
//    }

    private Supplier convertToEntity(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierDTO.getName());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setPhoneNumber(supplierDTO.getPhoneNumber());
        supplier.setContactEmail(supplierDTO.getContactEmail());

        return supplier;
    }
}
package com.pim.service;

import com.pim.model.Supplier;
import com.pim.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(UUID id, Supplier updatedSupplier) {
        Supplier existingSupplier = getSupplierById(id);
        existingSupplier.setName(updatedSupplier.getName());
        existingSupplier.setContactEmail(updatedSupplier.getContactEmail());
        existingSupplier.setPhoneNumber(updatedSupplier.getPhoneNumber());
        existingSupplier.setAddress(updatedSupplier.getAddress());
        return supplierRepository.save(existingSupplier);
    }

    public void deleteSupplier(UUID id) {
        supplierRepository.deleteById(id);
    }
}
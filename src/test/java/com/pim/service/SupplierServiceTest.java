package com.pim.service;

import com.pim.model.dto.SupplierDTO;
import com.pim.model.entity.Supplier;
import com.pim.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(SupplierServiceTest.class);

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private SupplierDTO supplierDTO;
    private UUID supplierId;

    @BeforeEach
    public void setUp() {
        supplierId = UUID.randomUUID();
        supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");

        supplierDTO = new SupplierDTO();
        supplierDTO.setName("Test Supplier");

        logger.info("Set up test data with supplierId: {}", supplierId);
    }

    @Test
    public void testGetAllSuppliers() {
        logger.info("Starting testGetAllSuppliers");
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        assertNotNull(suppliers);
        assertEquals(1, suppliers.size());
        assertEquals(supplier, suppliers.get(0));
        logger.info("Finished testGetAllSuppliers");
    }

    @Test
    public void testGetSupplierById() {
        logger.info("Starting testGetSupplierById");
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        Supplier foundSupplier = supplierService.getSupplierById(supplierId);
        assertNotNull(foundSupplier);
        assertEquals(supplier, foundSupplier);
        logger.info("Finished testGetSupplierById");
    }

    @Test
    public void testGetSupplierById_NotFound() {
        logger.info("Starting testGetSupplierById_NotFound");
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RuntimeException.class, () ->
                supplierService.getSupplierById(supplierId)
        );
        assertEquals("Supplier not found", exception.getMessage());
        logger.info("Finished testGetSupplierById_NotFound");
    }

    @Test
    public void testCreateSupplier() {
        logger.info("Starting testCreateSupplier");
        Supplier newSupplier = new Supplier();
        newSupplier.setName("Test Supplier");

        doReturn(newSupplier).when(supplierRepository).save(any(Supplier.class));
        Supplier createdSupplier = supplierService.createSupplier(supplierDTO);

        assertNotNull(createdSupplier);
        assertEquals(newSupplier.getName(), createdSupplier.getName());
        logger.info("Finished testCreateSupplier");
    }

    @Test
    public void testUpdateSupplier() {
        logger.info("Starting testUpdateSupplier");
        UUID nonExistentSupplierId = UUID.randomUUID();
        SupplierDTO updatedSupplierDTO = new SupplierDTO();
        updatedSupplierDTO.setName("Updated Supplier");

        when(supplierRepository.findById(nonExistentSupplierId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            supplierService.updateSupplier(nonExistentSupplierId, updatedSupplierDTO);
        });

        assertEquals("Supplier with id " + nonExistentSupplierId + " not found.", exception.getMessage());
        verify(supplierRepository, times(1)).findById(nonExistentSupplierId);
        verify(supplierRepository, times(0)).save(any(Supplier.class));

        logger.info("Finished testUpdateSupplier");
    }

    @Test
    public void testDeleteSupplier() {
        logger.info("Starting testDeleteSupplier");
        doNothing().when(supplierRepository).deleteById(supplierId);
        supplierService.deleteSupplier(supplierId);
        verify(supplierRepository, times(1)).deleteById(supplierId);
        logger.info("Finished testDeleteSupplier");
    }
}
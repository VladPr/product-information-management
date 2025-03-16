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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier supplier;
    private UUID supplierId;

    @BeforeEach
    void setUp() {
        supplierId = UUID.randomUUID();
        supplier = new Supplier();
        supplier.setId(supplierId);
        supplier.setName("Test Supplier");
    }

    @Test
    void testGetAllSuppliers() {
        when(supplierRepository.findAll()).thenReturn(List.of(supplier));

        List<Supplier> suppliers = supplierService.getAllSuppliers();
        assertNotNull(suppliers);
        assertEquals(1, suppliers.size());
    }

    @Test
    void testGetSupplierById_Success() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));

        Supplier foundSupplier = supplierService.getSupplierById(supplierId);
        assertNotNull(foundSupplier);
        assertEquals("Test Supplier", foundSupplier.getName());
    }

    @Test
    void testGetSupplierById_NotFound() {
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> supplierService.getSupplierById(supplierId));
    }

    @Test
    void testCreateSupplier_Success() {
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setName("New Supplier");

        Supplier newSupplier = new Supplier();
        newSupplier.setId(UUID.randomUUID()); // Simulating DB-generated ID
        newSupplier.setName("New Supplier");

        // ✅ Ensure repository does not find an existing supplier
        when(supplierRepository.findByName("New Supplier")).thenReturn(Optional.empty());

        // ✅ Ensure save() returns the newly created supplier
        when(supplierRepository.save(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier savedSupplier = invocation.getArgument(0);
            savedSupplier.setId(UUID.randomUUID()); // Simulate ID assignment
            return savedSupplier;
        });

        List<Supplier> createdSuppliers = supplierService.createSuppliers(List.of(supplierDTO));

        assertNotNull(createdSuppliers);
        assertEquals(1, createdSuppliers.size(), "Expected one supplier to be created");
    }





    @Test
    void testDeleteSupplier_Success() {
        when(supplierRepository.existsById(supplierId)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(supplierId);

        supplierService.deleteSupplier(supplierId);

        verify(supplierRepository, times(1)).deleteById(supplierId);
    }

    @Test
    void testDeleteSupplier_NotFound() {
        when(supplierRepository.existsById(supplierId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> supplierService.deleteSupplier(supplierId));
    }
}

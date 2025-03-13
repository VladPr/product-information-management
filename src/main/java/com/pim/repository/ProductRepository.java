package com.pim.repository;

import com.pim.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySku(String sku);
    Optional<List<Product>> findByBrandId(UUID id);
    Optional<List<Product>> findBySupplierId(UUID id);
    Optional<List<Product>> findByCategoryId(UUID id);


}

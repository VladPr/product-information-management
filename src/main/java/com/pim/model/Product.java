package com.pim.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String sku;

    @ElementCollection
    @CollectionTable(name = "product_name_translations", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "language_code") // Store language codes like "en", "fr", etc.
    @Column(name = "name")
    private Map<String, String> name;

    @ElementCollection
    @CollectionTable(name = "product_description_translations", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "language_code")
    @Column(name = "description")
    private Map<String, String> description;


    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private UUID brandId;

    @Column(nullable = false)
    private UUID supplierId;

    @Column(nullable = false)
    private UUID priceId;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;
}
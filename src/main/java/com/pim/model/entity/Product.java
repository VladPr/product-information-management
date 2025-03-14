package com.pim.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String sku;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<ProductNameTranslation> nameTranslations;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Set<ProductDescriptionTranslation> descriptionTranslations;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonBackReference
    private Brand brand;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "supplier_id", nullable = false )
    @JsonBackReference
    private Supplier supplier;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn (name = "price_id", referencedColumnName = "id")
//    private Price price;
}
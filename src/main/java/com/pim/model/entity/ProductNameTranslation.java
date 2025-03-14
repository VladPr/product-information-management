package com.pim.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table (name = "product_name_translation")
public class ProductNameTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "name_translation", nullable = false)
    private String nameTranslation;

    @ManyToOne (cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;


    public ProductNameTranslation() {
    }

    public ProductNameTranslation(String languageCode, String nameTranslation, Product product) {
        this.languageCode = languageCode;
        this.nameTranslation = nameTranslation;
        this.product = product;
    }
}
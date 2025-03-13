package com.pim.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "category_name_translation")
public class CategoryNameTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "name_translation", nullable = false)
    private String nameTranslation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    public CategoryNameTranslation() {
    }

    public CategoryNameTranslation(String languageCode, String nameTranslation, Category category) {
        this.languageCode = languageCode;
        this.nameTranslation = nameTranslation;
        this.category = category;
    }
}
package com.pim.api.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import lombok.*;
    @Getter
    @Setter

@Entity
public class Category {

    @Id
    @GeneratedValue
    private UUID id;

    @ElementCollection
    @CollectionTable(name = "category_name_translations", joinColumns = @JoinColumn(name = "category_id"))
    @MapKeyColumn(name = "language")
    @Column(name = "name")
    private Map<String, String> name;

    @Column(nullable = true)
    private UUID parentCategoryId;

    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp updatedAt;

}
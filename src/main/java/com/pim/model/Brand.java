package com.pim.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
    @Getter
    @Setter

@Entity
public class Brand {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

}
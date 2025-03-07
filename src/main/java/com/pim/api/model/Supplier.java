package com.pim.api.model;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
    @Getter
    @Setter


@Entity
public class Supplier {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

}
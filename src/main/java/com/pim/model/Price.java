package com.pim.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.*;
    @Getter
    @Setter


@Entity
public class Price {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Timestamp validFrom;

    @Column(nullable = true)
    private Timestamp validTo;

}
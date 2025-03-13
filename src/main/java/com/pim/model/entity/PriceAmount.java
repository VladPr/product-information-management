package com.pim.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PriceAmount {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "price_id", nullable = false)
    @JsonBackReference
    private Price price;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private BigDecimal amount;
}
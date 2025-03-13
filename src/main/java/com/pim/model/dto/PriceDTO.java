package com.pim.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PriceDTO {
    private String productSku;
    private Map<String, BigDecimal> amount;
}
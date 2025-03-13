package com.pim.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ProductDTO {


    private String sku;

    @JsonProperty("nameTranslations")
    private Map<String, String> nameTranslations;

   @JsonProperty("description")
    private Map<String, String> description;

    private String categoryName;
    private String brandName;
    private String supplierName;
}
package com.pim.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
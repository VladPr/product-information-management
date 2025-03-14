package com.pim.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class CategoryDTO {

    @JsonProperty("categories")
    private Map<String, Map<String, String>> categories; // Map<CategoryName, Map<LangCode, Translation>>

    private String name;

    public String getName(String categoryName, String languageCode) {
        if (categories != null && categories.containsKey(categoryName)) {
            return categories.get(categoryName).get(languageCode);
        }
        return null;
    }
}

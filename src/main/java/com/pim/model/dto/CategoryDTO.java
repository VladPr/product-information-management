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

package com.pim.controller;

import com.pim.model.dto.LanguageDTO;
import com.pim.model.entity.Language;
import com.pim.service.LanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService languageService;

    public LanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @GetMapping("/read/all")
    public List<Language> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    @GetMapping("/read/{id}")
    public Language getLanguageById(@PathVariable UUID id) {
        return languageService.getLanguageById(id);
    }

    @PostMapping("/create")
    public List<Language> createLanguages(@RequestBody List<LanguageDTO> languageDTOs) {
        return languageService.createLanguages(languageDTOs);
    }

    @PutMapping("/update/{id}")
    public Language updateLanguage(@PathVariable UUID id, @RequestBody LanguageDTO languageDTO) {
        return languageService.updateLanguage(id, languageDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLanguage(@PathVariable UUID id) {
        languageService.deleteLanguage(id);
    }

    @DeleteMapping("/delete/delete-all")
    public ResponseEntity<Void> deleteAllLanguages() {
        languageService.deleteAllLanguages();
        return ResponseEntity.noContent().build();
    }
}
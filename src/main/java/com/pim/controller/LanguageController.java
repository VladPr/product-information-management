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

    @GetMapping
    public List<Language> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    @GetMapping("/{id}")
    public Language getLanguageById(@PathVariable UUID id) {
        return languageService.getLanguageById(id);
    }

    @PostMapping
    public Language createLanguage(@RequestBody LanguageDTO languageDTO) {
        return languageService.createLanguage(languageDTO);
    }

    @PutMapping("/{id}")
    public Language updateLanguage(@PathVariable UUID id, @RequestBody LanguageDTO languageDTO) {
        return languageService.updateLanguage(id, languageDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteLanguage(@PathVariable UUID id) {
        languageService.deleteLanguage(id);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllLanguages() {
        languageService.deleteAllLanguages();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public Long countLanguages() {
        return languageService.countLanguages();
    }

    @PostMapping("/batch-upload")
    public ResponseEntity<List<Language>> batchUploadLanguages(@RequestBody List<LanguageDTO> languagesDTO) {
        List<Language> savedLanguages = languageService.saveAllLanguages(languagesDTO);
        return ResponseEntity.ok(savedLanguages);
    }
}
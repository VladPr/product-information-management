package com.pim.service;

import com.pim.model.Language;
import com.pim.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Language getLanguageById(UUID id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));
    }

    public Language createLanguage(Language language) {
        return languageRepository.save(language);
    }

    public Language updateLanguage(UUID id, Language updatedLanguage) {
        Language existingLanguage = getLanguageById(id);
        existingLanguage.setCode(updatedLanguage.getCode());
        existingLanguage.setName(updatedLanguage.getName());
        return languageRepository.save(existingLanguage);
    }

    public void deleteLanguage(UUID id) {
        languageRepository.deleteById(id);
    }
}
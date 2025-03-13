package com.pim.service;

import com.pim.model.dto.LanguageDTO;
import com.pim.model.entity.Language;
import com.pim.repository.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;
    private static final Logger logger = LoggerFactory.getLogger(LanguageService.class);

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    public List<Language> getAllLanguages() {
        logger.info("Fetching all languages from the database.");
        return languageRepository.findAll();
    }

    public Language getLanguageById(UUID id) {
        logger.info("Fetching language with id: {}", id);
        return languageRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Language with id {} not found.", id);
                    return new EntityNotFoundException("Language with id " + id + " not found.");
                });
    }

    public Language createLanguage(LanguageDTO languageDTO) {
        logger.info("Attempting to create a new language: {}", languageDTO);

        if (languageDTO.getName() == null || languageDTO.getName().trim().isEmpty()) {
            throw new ConstraintViolationException("Language name cannot be null or empty", null);
        }

        Optional<Language> existingLanguage = languageRepository.findByCode(languageDTO.getCode());
        if (existingLanguage.isPresent()) {
            logger.warn("Duplicate language detected: {}", languageDTO.getName());
            throw new DataIntegrityViolationException("Language with name '" + languageDTO.getName() + "' already exists.");
        }

        Language language = convertToEntity(languageDTO);
        return languageRepository.save(language);
    }


    public Language updateLanguage(UUID id, LanguageDTO languageDTO) {
        logger.info("Updating language with id: {}", id);

        Language existingLanguage = languageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Language with id " + id + " not found."));

        existingLanguage.setCode(languageDTO.getCode());
        existingLanguage.setName(languageDTO.getName());

        return languageRepository.save(existingLanguage);
    }


    @Transactional
    public void deleteLanguage(UUID id) {
        logger.info("Deleting language with id: {}", id);
        Language language = languageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Language not found"));

        languageRepository.delete(language);
    }


    @Transactional
    public void deleteAllLanguages() {
        logger.warn("Deleting all languages from the database.");

        // Optional: Check if there are languages to delete
        long languageCount = languageRepository.count();
        if (languageCount == 0) {
            logger.warn("No languages found to delete.");
            return;
        }

        try {
            languageRepository.deleteAllInBatch();
            logger.info("Successfully deleted all languages from the database.");
        } catch (Exception e) {
            logger.error("Error occurred while deleting all languages: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete all languages", e);  // Rethrow or handle accordingly
        }
    }

    public Long countLanguages() {
        long count = languageRepository.count();
        logger.info("Total number of languages: {}", count);
        return count;
    }

    public List<Language> saveAllLanguages(List<LanguageDTO> languageDTOs) {
        logger.info("Saving all languages");
        List<Language> languages = languageDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        return languageRepository.saveAll(languages);
    }

    private Language convertToEntity(LanguageDTO languageDTO) {
        Language language = new Language();
        language.setName(languageDTO.getName());
        language.setCode(languageDTO.getCode());
        return language;
    }
}

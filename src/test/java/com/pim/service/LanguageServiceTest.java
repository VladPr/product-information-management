package com.pim.service;

import com.pim.model.Language;
import com.pim.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LanguageServiceTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private LanguageService languageService;

    private Language language;
    private UUID languageId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        languageId = UUID.randomUUID();
        language = new Language();
        language.setId(languageId);
        language.setCode("en");
        language.setName("English");
    }

    @Test
    void testGetAllLanguages() {
        languageService.getAllLanguages();
        verify(languageRepository, times(1)).findAll();
    }

    @Test
    void testGetLanguageById() {
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));
        Language foundLanguage = languageService.getLanguageById(languageId);
        assertEquals(language, foundLanguage);
    }

    @Test
    void testCreateLanguage() {
        when(languageRepository.save(language)).thenReturn(language);
        Language createdLanguage = languageService.createLanguage(language);
        assertEquals(language, createdLanguage);
    }

    @Test
    void testUpdateLanguage() {
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));
        when(languageRepository.save(language)).thenReturn(language);
        Language updatedLanguage = languageService.updateLanguage(languageId, language);
        assertEquals(language, updatedLanguage);
    }

    @Test
    void testDeleteLanguage() {
        languageService.deleteLanguage(languageId);
        verify(languageRepository, times(1)).deleteById(languageId);
    }
}
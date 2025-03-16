package com.pim.service;

import com.pim.model.entity.Language;
import com.pim.repository.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private LanguageService languageService;

    private Language language;
    private UUID languageId;

    @BeforeEach
    void setUp() {
        languageId = UUID.randomUUID();
        language = new Language();
        language.setId(languageId);
        language.setName("English");
        language.setCode("en");
    }

    @Test
    void testGetAllLanguages() {
        when(languageRepository.findAll()).thenReturn(List.of(language));

        List<Language> languages = languageService.getAllLanguages();
        assertNotNull(languages);
        assertEquals(1, languages.size());
    }

    @Test
    void testGetLanguageById_Success() {
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));

        Language foundLanguage = languageService.getLanguageById(languageId);
        assertNotNull(foundLanguage);
        assertEquals("English", foundLanguage.getName());
    }

    @Test
    void testGetLanguageById_NotFound() {
        when(languageRepository.findById(languageId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> languageService.getLanguageById(languageId));
    }
}

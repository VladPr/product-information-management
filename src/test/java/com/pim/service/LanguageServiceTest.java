package com.pim.service;

import com.pim.model.dto.LanguageDTO;
import com.pim.model.entity.Language;
import com.pim.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
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
    private LanguageDTO languageDTO;
    private UUID languageId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        languageId = UUID.randomUUID();
        language = new Language();
        language.setId(languageId);
        language.setCode("en");
        language.setName("English");

        languageDTO = new LanguageDTO();
        languageDTO.setCode("en");
        languageDTO.setName("English");
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
    void testCreateLanguages() {
        when(languageRepository.saveAll(anyList())).thenReturn(List.of(language));
        List<Language> createdLanguages = languageService.createLanguages(List.of(languageDTO));
        assertEquals(1, createdLanguages.size());
        assertEquals(language.getCode(), createdLanguages.get(0).getCode());
        assertEquals(language.getName(), createdLanguages.get(0).getName());
    }

    @Test
    void testUpdateLanguage() {
        when(languageRepository.findById(languageId)).thenReturn(Optional.of(language));
        when(languageRepository.save(any(Language.class))).thenReturn(language);
        Language updatedLanguage = languageService.updateLanguage(languageId, languageDTO);
        assertEquals(language.getCode(), updatedLanguage.getCode());
        assertEquals(language.getName(), updatedLanguage.getName());
    }

    @Test
    void testDeleteLanguage() {
        when(languageRepository.findById(languageId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            languageService.deleteLanguage(languageId);
        });

        assertEquals("Language not found", exception.getMessage());
        verify(languageRepository, times(1)).findById(languageId);
        verify(languageRepository, times(0)).deleteById(languageId);
    }
}
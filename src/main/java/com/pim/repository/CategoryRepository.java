package com.pim.repository;

import com.pim.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
        boolean existsByNameTranslations_NameTranslation(String nameTranslation);
        Optional<Category> findByNameTranslations_NameTranslation(String nameTranslation);
}


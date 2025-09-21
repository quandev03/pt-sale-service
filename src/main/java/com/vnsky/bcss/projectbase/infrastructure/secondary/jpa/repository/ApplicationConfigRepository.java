package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.ApplicationConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationConfigRepository extends JpaRepository<ApplicationConfigEntity, String> {
    List<ApplicationConfigEntity> getByTypeAndLanguage(String type, String language);

    Optional<ApplicationConfigEntity> findByTypeAndCode(String tableName, String columnName);
}

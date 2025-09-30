package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.ApplicationConfigEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ApplicationConfigRepository extends JpaRepository<ApplicationConfigEntity, String> {
    List<ApplicationConfigEntity> getByTypeAndLanguage(String type, String language);

    List<ApplicationConfigEntity> getByType(String type);

    Optional<ApplicationConfigEntity> findByTypeAndCode(String tableName, String columnName);

    @Query(value = """
        Select CODE AS COUNTRY_CODE,
            NAME as COUNTRY_NAME
        from APPLICATION_CONFIG
        where type = :type
    """, nativeQuery = true)
    List<Tuple> getCountryCodes(String type);
}

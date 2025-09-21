package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.StockIsdnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockIsdnRepository extends JpaRepository<StockIsdnEntity, String> {

    @Query(value = "SELECT s FROM StockIsdnEntity s WHERE s.status = 1 AND s.activeDatetime is null ORDER BY s.id ASC")
    List<StockIsdnEntity> findAvailableIsdns();

    Optional<StockIsdnEntity> findByIsdn(Long isdn);

    Optional<StockIsdnEntity> findBySerial(Long serial);
}

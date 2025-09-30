package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.SaleOrderLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleOrderLineRepository extends JpaRepository<SaleOrderLineEntity, String> {
    List<SaleOrderLineEntity> findBySaleOrderId(String saleOrderId);
} 
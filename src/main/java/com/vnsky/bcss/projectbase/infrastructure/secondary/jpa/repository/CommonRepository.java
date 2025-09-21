package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
@RequiredArgsConstructor
public class CommonRepository {

    private final EntityManager em;

    public int getSequenceValue(String sequenceName) {
        return ((BigDecimal) em.createNativeQuery("SELECT " + sequenceName + ".nextval from DUAL").getSingleResult()).intValue();
    }
}

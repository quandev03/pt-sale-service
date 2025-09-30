package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.AgentDebitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgentDebitRepository extends BaseJPARepository<AgentDebitEntity, String> {
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END
            FROM AgentDebitEntity a
            WHERE a.orgId = :orgId
              AND a.paymentId = :paymentId
        """)
    boolean existsByPaymentId(String paymentId, String orgId);

    @Query("""
        SELECT a
        FROM AgentDebitEntity a
        WHERE (:orgId IS NULL OR a.orgId = :orgId)
          AND (:q IS NULL OR LOWER(a.paymentId) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:type IS NULL OR a.type = :type)
          AND (:startDate IS NULL OR a.createdDate >= :startDate)
          AND (:endDate IS NULL OR a.createdDate <= :endDate)
        ORDER BY a.createdDate DESC
    """)
    Page<AgentDebitEntity> search(String q, String type, String orgId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("""
        SELECT a
        FROM AgentDebitEntity a
        WHERE (:orgId IS NULL OR a.orgId = :orgId)
    """)
    List<AgentDebitEntity> findAll(String orgId);
}

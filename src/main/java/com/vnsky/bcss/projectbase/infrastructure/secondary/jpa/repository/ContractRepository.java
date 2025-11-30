package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.ContractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ContractRepository extends BaseJPARepository<ContractEntity, String> {

    @Query(value = """
        SELECT c FROM ContractEntity c
        WHERE (:ownerName IS NULL OR LOWER(c.ownerName) LIKE LOWER(CONCAT('%', :ownerName, '%')))
          AND (:tenantName IS NULL OR LOWER(c.tenantName) LIKE LOWER(CONCAT('%', :tenantName, '%')))
          AND (:fromDate IS NULL OR c.createdDate >= :fromDate)
          AND (:toDate IS NULL OR c.createdDate <= :toDate)
        ORDER BY c.createdDate DESC
        """)
    Page<ContractEntity> search(@Param("ownerName") String ownerName,
                                 @Param("tenantName") String tenantName,
                                 @Param("fromDate") LocalDateTime fromDate,
                                 @Param("toDate") LocalDateTime toDate,
                                 Pageable pageable);
}


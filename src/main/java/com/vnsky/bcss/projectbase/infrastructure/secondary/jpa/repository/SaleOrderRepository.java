package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.SaleOrderEntity;
import com.vnsky.bcss.projectbase.domain.entity.StockIsdnEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrderEntity, String> {
    @Query(value = """
        SELECT so.* FROM SALE_ORDER so
        JOIN PACKAGE_PROFILE pp ON pp.PCK_CODE = so.PCK_CODE
        WHERE pp.PCK_PRICE = 0
        AND so.ORG_ID = :orgId
    """, nativeQuery = true)
    Page<SaleOrderEntity> findBookFreeByOrgId(Pageable pageable,@Param("orgId") String orgId);
}

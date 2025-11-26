package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitImageEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganizationUnitImageRepository extends BaseJPARepository<OrganizationUnitImageEntity, String> {

    List<OrganizationUnitImageEntity> findByOrgUnitIdOrderBySortOrderAsc(String orgUnitId);

    @Modifying
    @Query("DELETE FROM OrganizationUnitImageEntity e WHERE e.orgUnitId = :orgUnitId")
    void deleteByOrgUnitId(@Param("orgUnitId") String orgUnitId);
}



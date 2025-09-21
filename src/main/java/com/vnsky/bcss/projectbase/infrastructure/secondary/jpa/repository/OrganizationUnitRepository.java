package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationUnitRepository extends BaseJPARepository<OrganizationUnitEntity, String> {

    @Query(value = """
        SELECT ID, PARENT_ID , ORG_CODE , ORG_NAME FROM ORGANIZATION_UNIT
    """, nativeQuery = true)
    List<Tuple> getInfoOrganization();

    @Query(value = """
        SELECT ID, PARENT_ID , ORG_CODE , ORG_NAME
              FROM ORGANIZATION_UNIT ou
              START WITH ou.ID = :orgId
              CONNECT BY PRIOR ou.ID = ou.PARENT_ID
    """, nativeQuery = true)
    List<Tuple> getInfoOrganizationByParentId(@Param("orgId") String orgId);
}

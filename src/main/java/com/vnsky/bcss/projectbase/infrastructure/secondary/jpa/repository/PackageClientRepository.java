package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.PackageClientEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageClientRepository extends BaseJPARepository<PackageClientEntity, String> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM PACKAGE_CLIENT WHERE CLIENT_ID = :clientId", nativeQuery = true)
    void deleteByClientId(@Param("clientId") String clientId);
}

package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.RoomServiceEntity;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomServiceRepository extends BaseJPARepository<RoomServiceEntity, String> {

    Optional<RoomServiceEntity> findByClientIdAndServiceCode(String clientId, String serviceCode);

    Optional<RoomServiceEntity> findByClientIdAndServiceCodeAndIdNot(String clientId, String serviceCode, String id);

    List<RoomServiceEntity> findByOrgUnitIdOrderByCreatedDateDesc(String orgUnitId);

    List<RoomServiceEntity> findByClientIdOrderByCreatedDateDesc(String clientId);

    @Query("SELECT r FROM RoomServiceEntity r WHERE r.clientId = :clientId " +
           "AND (:orgUnitId IS NULL OR r.orgUnitId = :orgUnitId) " +
           "AND (:serviceType IS NULL OR r.serviceType = :serviceType) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "ORDER BY r.createdDate DESC")
    List<RoomServiceEntity> findByFilters(@Param("clientId") String clientId,
                                          @Param("orgUnitId") String orgUnitId,
                                          @Param("serviceType") RoomServiceType serviceType,
                                          @Param("status") Integer status);
}


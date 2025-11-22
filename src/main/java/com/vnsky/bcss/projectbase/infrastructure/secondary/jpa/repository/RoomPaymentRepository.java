package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.RoomPaymentEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomPaymentRepository extends BaseJPARepository<RoomPaymentEntity, String> {

    Optional<RoomPaymentEntity> findByOrgUnitIdAndMonthAndYear(String orgUnitId, Integer month, Integer year);

    List<RoomPaymentEntity> findByOrgUnitIdOrderByYearDescMonthDesc(String orgUnitId);

    @Query("SELECT rp FROM RoomPaymentEntity rp WHERE rp.orgUnitId = :orgUnitId " +
           "AND (:year IS NULL OR rp.year = :year) " +
           "AND (:month IS NULL OR rp.month = :month) " +
           "ORDER BY rp.year DESC, rp.month DESC")
    List<RoomPaymentEntity> findByFilters(@Param("orgUnitId") String orgUnitId,
                                          @Param("year") Integer year,
                                          @Param("month") Integer month);
}

